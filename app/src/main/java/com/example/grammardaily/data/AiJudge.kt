package com.example.grammardaily.data

import android.content.Context
import android.net.Uri
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class JudgeResult(
    val correct: Boolean,
    val note: String,
    val byAi: Boolean
)

object AiJudge {

    private const val DIR = "ai_teacher"
    private const val FILE = "teacher.task"

    @Volatile
    private var llm: LlmInference? = null

    @Volatile
    private var loadFailed = false

    fun modelFile(context: Context): File {
        val dir = File(context.filesDir, DIR)
        if (!dir.exists()) dir.mkdirs()
        return File(dir, FILE)
    }

    fun isModelReady(context: Context): Boolean = modelFile(context).length() > 1_000_000

    fun deleteModel(context: Context) {
        try {
            llm?.close()
        } catch (e: Exception) {
        }
        llm = null
        loadFailed = false
        val f = modelFile(context)
        if (f.exists()) f.delete()
    }

    fun importModel(context: Context, uri: Uri): Boolean {
        return try {
            val target = modelFile(context)
            val tmp = File(target.absolutePath + ".tmp")
            context.contentResolver.openInputStream(uri)?.use { input ->
                tmp.outputStream().use { output -> input.copyTo(output) }
            } ?: return false
            if (tmp.length() < 1_000_000) {
                tmp.delete()
                return false
            }
            if (target.exists()) target.delete()
            tmp.renameTo(target)
            llm = null
            loadFailed = false
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun ensureLoaded(context: Context): LlmInference? {
        if (isModelReady(context)) {
            val existing = llm
            if (existing != null) return existing
            if (!loadFailed) {
                try {
                    val options = LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(modelFile(context).absolutePath)
                        .setMaxTokens(256)
                        .setTopK(1)
                        .setTemperature(0.0f)
                        .build()
                    llm = LlmInference.createFromOptions(context.applicationContext, options)
                } catch (e: Exception) {
                    loadFailed = true
                    llm = null
                }
            }
        }
        return llm
    }

    fun aiReady(context: Context): Boolean = ensureLoaded(context) != null

    private fun tokens(text: String): List<String> =
        ExerciseEngine.normalize(text)
            .split(" ")
            .filter { it.isNotBlank() }

    private fun multiset(words: List<String>): Map<String, Int> =
        words.groupingBy { it }.eachCount()

    private fun fuzzyIn(given: String, pool: List<String>): Boolean =
        pool.any { ExerciseEngine.closeEnough(given, it) }

    private fun coverage(targetParts: List<String>, givenTokens: List<String>): Double {
        if (targetParts.isEmpty()) return 0.0
        val hit = targetParts.count { part ->
            fuzzyIn(ExerciseEngine.normalize(part), givenTokens)
        }
        return hit.toDouble() / targetParts.size
    }

    private fun heuristic(context: Context, exercise: Exercise, given: String): JudgeResult? {
        val input = ExerciseEngine.normalize(given)
        if (input.isBlank()) return null

        if (ExerciseEngine.closeEnough(given, exercise.answer)) {
            return JudgeResult(true, "Exact match.", false)
        }

        val givenTokens = tokens(given)
        val answerTokens = tokens(exercise.answer)

        if (multiset(givenTokens) == multiset(answerTokens) && givenTokens.isNotEmpty()) {
            return JudgeResult(true, "Same words, different order \u2014 the grammar pieces are all there.", false)
        }

        val mustInclude: List<String> = when (exercise.kind) {
            ExKind.FIX -> {
                val wrongTokens = tokens(exercise.sentence).toSet()
                answerTokens.filter { it !in wrongTokens && it.length >= 2 }
            }
            else -> answerTokens.filter { it.length >= 2 }
        }

        val targetParts = when (exercise.kind) {
            ExKind.FIX -> mustInclude
            ExKind.TYPE -> {
                val accepted = exercise.accepted.firstOrNull()
                if (accepted.isNullOrBlank()) mustInclude else listOf(accepted.trim())
            }
            else -> mustInclude
        }

        if (targetParts.isNotEmpty() && givenTokens.isNotEmpty()) {
            val cov = coverage(targetParts, givenTokens)
            val answerOverlap = answerTokens.count { it in givenTokens }.toDouble() /
                answerTokens.size.coerceAtLeast(1)
            if (cov >= 0.6 && answerOverlap >= 0.4) {
                return JudgeResult(
                    true,
                    "Accepted \u2014 different wording, but the target grammar is in your sentence.",
                    false
                )
            }
        }

        return null
    }


    data class DownloadableModel(val name: String, val sizeMb: Int, val url: String)

    val downloadableModels = listOf(
        DownloadableModel(
            "Gemma 3 1B (recommended, 529 MB)",
            529,
            "https://huggingface.co/ekyra15/vaya-gemma3-1b-it-int4/resolve/main/gemma3-1b-it-int4.task"
        ),
        DownloadableModel(
            "Gemma 3 1B mirror (529 MB)",
            529,
            "https://huggingface.co/meldgaard/ilt-gemma3-1b-it-litert/resolve/main/gemma3-1b-it.task"
        )
    )

    fun downloadBlocking(context: Context, url: String, onProgress: (downloaded: Long, total: Long) -> Unit): Boolean {
        val target = modelFile(context)
        val tmp = File(target.absolutePath + ".downloading")
        try {
            tmp.outputStream().use { output ->
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.instanceFollowRedirects = true
                conn.connectTimeout = 30000
                conn.readTimeout = 60000
                conn.connect()
                if (conn.responseCode !in 200..299) {
                    conn.disconnect()
                    return false
                }
                val total = conn.contentLengthLong
                conn.inputStream.use { input ->
                    val buf = ByteArray(1 shl 16)
                    var read = 0L
                    var n = input.read(buf)
                    while (n >= 0) {
                        output.write(buf, 0, n)
                        read += n
                        if (total > 0) onProgress(read, total)
                        n = input.read(buf)
                    }
                }
                conn.disconnect()
            }
            if (tmp.length() < 1_000_000) {
                tmp.delete()
                return false
            }
            if (target.exists()) target.delete()
            tmp.renameTo(target)
            llm = null
            loadFailed = false
            return true
        } catch (e: Exception) {
            tmp.delete()
            return false
        }
    }

    private fun writtenSummary(results: List<ExerciseResult>): String {
        val typed = results.filter { it.given != null && it.given!!.isNotBlank() }
        if (typed.isEmpty()) return "(no written answers)"
        return typed.take(10).joinToString("\n") { r ->
            "- wrote: \"" + r.given!!.trim().take(120) + "\" -> " + if (r.correct) "accepted" else "rejected"
        }
    }

    fun levelReportBlocking(
        context: Context,
        level: Level,
        scores: List<Triple<Topic, Int, Int>>,
        results: List<ExerciseResult>
    ): String {
        val model = ensureLoaded(context)
        if (model != null) {
            val scoreLines = scores.joinToString("\n") { (topic, c, t) ->
                "${topic.id} ${topic.title}: $c of $t correct"
            }
            val prompt = buildString {
                append("You are an English grammar teacher. A student took a test.\n")
                append("Results per topic:\n").append(scoreLines).append("\n")
                append("The student's written answers were read and checked like this:\n")
                append(writtenSummary(results)).append("\n\n")
                append("Decide the student's grammar level (A1, A2, B1, B2 or C1) and what to learn next.\n")
                append("Reply in exactly this format, with nothing before or after:\n")
                append("LEVEL: <one letter-number code>\n")
                append("- <one short line what to learn, max 5 lines, simple words>")
            }
            val raw = try {
                model.generateResponse(prompt)
            } catch (e: Exception) {
                null
            }
            if (!raw.isNullOrBlank()) {
                val lines = raw.trim().lines().filter { it.isNotBlank() }
                val levelLine = lines.firstOrNull { it.uppercase().contains("LEVEL") }
                    ?.let { it.substringAfter(":").trim().take(30) }
                val bullets = lines.filter { it.trim().startsWith("-") }.take(5)
                if (bullets.isNotEmpty()) {
                    return buildString {
                        if (levelLine != null) append("AI judge: your grammar level is " + levelLine + ".\n")
                        append("What to learn next:\n")
                        bullets.forEach { append(it.trim()).append("\n") }
                    }.trim()
                }
            }
        }
        val weak = scores.filter { (topic, c, t) -> t > 0 && c < t }
            .sortedBy { (topic, c, t) -> c.toDouble() / t }
            .take(3)
        return buildString {
            append("Checked your answers without the AI model (import one below for an AI-judged report).\n")
            append("Level: " + level.code + " \u2014 " + level.title + ".\n")
            if (weak.isEmpty()) {
                append("No weak topics in this test \u2014 keep going with your daily lessons.")
            } else {
                append("What to learn next:\n")
                weak.forEach { (topic, c, t) -> append("- " + topic.title + " (" + c + "/" + t + " right)\n") }
            }
        }.trim()
    }

    fun judgeBlocking(context: Context, exercise: Exercise, given: String): JudgeResult {
        heuristic(context, exercise, given)?.let { return it }

        val model = ensureLoaded(context)
        if (model != null) {
            val target = exercise.accepted.firstOrNull() ?: exercise.answer
            val prompt = buildString {
                append("You are a strict but fair grammar teacher. ")
                append("The task: ").append(exercise.prompt).append("\n")
                if (exercise.sentence.isNotBlank()) {
                    append("Sentence: ").append(exercise.sentence).append("\n")
                }
                append("Model answer: ").append(target).append("\n")
                append("Student answer: ").append(given.trim()).append("\n")
                append("Does the student answer complete the task with correct grammar, ")
                append("even if the wording is different? ")
                append("Reply with exactly one word: YES or NO.")
            }
            val raw = try {
                model.generateResponse(prompt)
            } catch (e: Exception) {
                return JudgeResult(false, "The right answer is shown below \u2014 compare it with yours.", false)
            }
            val verdict = raw.trim().uppercase()
            return if (verdict.contains("YES") && !verdict.contains("NO")) {
                JudgeResult(true, "The AI judge accepted your answer.", true)
            } else {
                JudgeResult(false, "The right answer is shown below \u2014 compare it with yours.", true)
            }
        }

        return JudgeResult(false, "The right answer is shown below \u2014 compare it with yours.", false)
    }

    suspend fun judge(context: Context, exercise: Exercise, given: String): JudgeResult =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            judgeBlocking(context, exercise, given)
        }

    fun close(context: Context) {
        try {
            llm?.close()
        } catch (e: Exception) {
        }
        llm = null
    }
}

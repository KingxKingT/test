package com.example.grammardaily.data

import android.annotation.SuppressLint
import com.example.grammardaily.ai.AiJudgeService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume

data class JudgeResult(
    val correct: Boolean,
    val note: String,
    val byAi: Boolean
)

object AiJudge {

    private const val DIR = "ai_teacher"
    private const val FILE = "teacher.task"
    private const val PREFS = "ai_teacher_state"
    private const val KEY_TIMEOUT_COUNT = "timeout_count"
    private const val KEY_DISABLED = "disabled"
    private const val MAX_TIMEOUTS = 3
    private const val ANSWER_TIMEOUT_MS = 90_000L

    const val KEY_PROMPT = "judge_prompt"
    const val KEY_RECEIVER = "judge_receiver"
    const val KEY_RESPONSE = "judge_response"
    const val KEY_ERROR = "judge_error"

    fun modelFile(context: Context): File {
        val dir = File(context.filesDir, DIR)
        if (!dir.exists()) dir.mkdirs()
        return File(dir, FILE)
    }

    fun isModelReady(context: Context): Boolean = modelFile(context).length() > 1_000_000

    private fun aiPrefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isDisabled(context: Context): Boolean =
        aiPrefs(context).getBoolean(KEY_DISABLED, false)

    private fun markTimeout(context: Context) {
        val p = aiPrefs(context)
        val count = p.getInt(KEY_TIMEOUT_COUNT, 0) + 1
        p.edit()
            .putInt(KEY_TIMEOUT_COUNT, count)
            .putBoolean(KEY_DISABLED, count >= MAX_TIMEOUTS)
            .apply()
    }

    private fun markSuccess(context: Context) {
        aiPrefs(context).edit()
            .putInt(KEY_TIMEOUT_COUNT, 0)
            .putBoolean(KEY_DISABLED, false)
            .apply()
    }

    private fun resetFailures(context: Context) {
        aiPrefs(context).edit()
            .putInt(KEY_TIMEOUT_COUNT, 0)
            .putBoolean(KEY_DISABLED, false)
            .apply()
    }

    fun deleteModel(context: Context) {
        resetFailures(context)
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
            resetFailures(context)
            warmUp(context)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun warmUp(context: Context) {
        if (isDisabled(context) || !isModelReady(context)) return
        try {
            context.applicationContext.startService(
                Intent(context.applicationContext, AiJudgeService::class.java)
            )
        } catch (t: Throwable) {
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun serviceAskBlocking(context: Context, prompt: String): String? {
        return runBlocking {
            withTimeoutOrNull(ANSWER_TIMEOUT_MS) {
                suspendCancellableCoroutine { cont ->
                    val receiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
                        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                            val resp = resultData?.getString(KEY_RESPONSE)
                            if (cont.isActive) cont.resume(resp)
                        }
                    }
                    try {
                        val intent = Intent(context.applicationContext, AiJudgeService::class.java)
                            .putExtra(KEY_PROMPT, prompt)
                            .putExtra(KEY_RECEIVER, receiver)
                        context.applicationContext.startService(intent)
                    } catch (t: Throwable) {
                        if (cont.isActive) cont.resume(null)
                    }
                }
            }
        }
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
            resetFailures(context)
            warmUp(context)
            return true
        } catch (e: Exception) {
            tmp.delete()
            return false
        }
    }

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

    private val fallbackNote = "The right answer is shown below \u2014 compare it with yours."

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
        if (!isDisabled(context) && isModelReady(context)) {
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
            val raw = serviceAskBlocking(context, prompt)
            if (!raw.isNullOrBlank()) {
                markSuccess(context)
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
            } else {
                markTimeout(context)
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

        if (isDisabled(context) || !isModelReady(context)) {
            return JudgeResult(false, fallbackNote, false)
        }

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
        val raw = serviceAskBlocking(context, prompt)
        return if (raw.isNullOrBlank()) {
            markTimeout(context)
            JudgeResult(false, fallbackNote, false)
        } else {
            markSuccess(context)
            val verdict = raw.trim().uppercase()
            if (verdict.contains("YES") && !verdict.contains("NO")) {
                JudgeResult(true, "The AI judge accepted your answer.", true)
            } else {
                JudgeResult(false, fallbackNote, true)
            }
        }
    }

    suspend fun judge(context: Context, exercise: Exercise, given: String): JudgeResult =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            judgeBlocking(context, exercise, given)
        }
}

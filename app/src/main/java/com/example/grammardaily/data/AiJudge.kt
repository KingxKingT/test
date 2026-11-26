package com.example.grammardaily.data

import android.content.Context
import android.net.Uri
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File

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

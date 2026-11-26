package com.example.grammardaily.data

import android.content.Context

object EssayStore {

    private val prompts = listOf(
        "Should people share everything online? Argue both sides.",
        "Learning a language: talent or habit? Use your own story.",
        "Is money the best measure of success? Explain why, or why not.",
        "Describe a decision you got wrong and what it taught you.",
        "Should school start later in the day? Give your reasons.",
        "One invention changed your family's life. Tell the story.",
        "Are cities made for cars or for people? Propose one fix.",
        "Does social media make friendship easier or shallower?"
    )

    fun thisWeekPrompt(): String {
        val week = (java.time.LocalDate.now().toEpochDay() / 7).toInt()
        return prompts[Math.floorMod(week, prompts.size)]
    }

    private const val PREFS = "grammar_daily"
    private const val KEY_TEXT = "essay_text"
    private const val KEY_FEEDBACK = "essay_feedback"
    private const val KEY_HISTORY = "essay_history"

    fun load(context: Context): String =
        prefs(context).getString(KEY_TEXT, "").orEmpty()

    fun loadFeedback(context: Context): String? =
        prefs(context).getString(KEY_FEEDBACK, null)

    fun save(context: Context, text: String, feedback: String?) {
        val editor = prefs(context).edit()
            .putString(KEY_TEXT, text)
            .putString(KEY_FEEDBACK, feedback)
        if (feedback != null) {
            val score = Regex("SCORE\\s*:?\\s*(\\d+)\\s*/\\s*10", RegexOption.IGNORE_CASE)
                .find(feedback)?.groupValues?.get(1)?.toIntOrNull()
            if (score != null) {
                val week = weekNumber()
                val entries = prefs(context).getStringSet(KEY_HISTORY, emptySet())
                    .orEmpty().toMutableSet()
                entries.removeAll { it.split("|").firstOrNull() == week.toString() }
                entries.add(week.toString() + "|" + score)
                editor.putStringSet(KEY_HISTORY, entries)
            }
        }
        editor.apply()
    }

    fun history(context: Context): List<Pair<Int, Int>> =
        prefs(context).getStringSet(KEY_HISTORY, emptySet())
            .orEmpty()
            .mapNotNull { entry ->
                val parts = entry.split("|")
                val week = parts.getOrNull(0)?.toIntOrNull()
                val score = parts.getOrNull(1)?.toIntOrNull()
                if (week != null && score != null) week to score else null
            }
            .sortedByDescending { it.first }

    private fun weekNumber(): Int =
        (java.time.LocalDate.now().toEpochDay() / 7).toInt()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}

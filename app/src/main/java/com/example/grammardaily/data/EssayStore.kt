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

    fun load(context: Context): String =
        prefs(context).getString(KEY_TEXT, "").orEmpty()

    fun loadFeedback(context: Context): String? =
        prefs(context).getString(KEY_FEEDBACK, null)

    fun save(context: Context, text: String, feedback: String?) {
        prefs(context).edit()
            .putString(KEY_TEXT, text)
            .putString(KEY_FEEDBACK, feedback)
            .apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}

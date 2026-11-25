package com.example.grammardaily.data

import android.content.Context
import java.time.LocalDate

data class ProgressState(
    val placementDone: Boolean,
    val level: Level,
    val masteredTopicIds: Set<String>,
    val todayLessonTopicId: String?,
    val todayLessonDone: Boolean,
    val lessonDoneTopicId: String?,
    val streakDays: Int,
    val allMasteredFinished: Boolean
)

object ProgressStore {

    private const val PREFS = "grammar_daily"
    private const val KEY_PLACED = "placement_done"
    private const val KEY_LEVEL = "level_index"
    private const val KEY_MASTERED = "mastered_topics"
    private const val KEY_LESSON_DATE = "lesson_date"
    private const val KEY_LESSON_TOPIC = "lesson_topic_id"
    private const val KEY_STREAK = "streak_days"
    private const val KEY_LAST_STUDY = "last_study_date"

    fun readState(context: Context): ProgressState {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val placed = prefs.getBoolean(KEY_PLACED, false)
        val level = Level.fromIndex(prefs.getInt(KEY_LEVEL, 0))
        val mastered = prefs.getStringSet(KEY_MASTERED, emptySet())?.toSet() ?: emptySet()
        val today = LocalDate.now().toString()
        val lessonDate = prefs.getString(KEY_LESSON_DATE, null)
        val lessonTopic = prefs.getString(KEY_LESSON_TOPIC, null)
        val todayDone = lessonDate == today && lessonTopic != null
        val nextTopic = GrammarCurriculum.nextTopic(level, mastered)
        val levelMastered = GrammarCurriculum.levelIsMastered(level, mastered)
        return ProgressState(
            placementDone = placed,
            level = level,
            masteredTopicIds = mastered,
            todayLessonTopicId = if (!placed) null else nextTopic?.id,
            todayLessonDone = todayDone,
            lessonDoneTopicId = if (todayDone) lessonTopic else null,
            streakDays = prefs.getInt(KEY_STREAK, 0),
            allMasteredFinished = level == Level.C1 && levelMastered
        )
    }

    fun markPlacement(level: Level, context: Context) {
        prefs(context).edit()
            .putBoolean(KEY_PLACED, true)
            .putInt(KEY_LEVEL, level.ordinal)
            .remove(KEY_LESSON_DATE)
            .remove(KEY_LESSON_TOPIC)
            .apply()
    }

    fun recordQuizResult(context: Context) {
        val prefs = prefs(context)
        val today = LocalDate.now()
        val last = prefs.getString(KEY_LAST_STUDY, null)?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val newStreak = when (last) {
            today -> prefs.getInt(KEY_STREAK, 0)
            today.minusDays(1) -> prefs.getInt(KEY_STREAK, 0) + 1
            else -> 1
        }
        prefs.edit()
            .putInt(KEY_STREAK, newStreak)
            .putString(KEY_LAST_STUDY, today.toString())
            .apply()
    }

    fun markTopicMastered(context: Context, topicId: String) {
        val prefs = prefs(context)
        val mastered = prefs.getStringSet(KEY_MASTERED, emptySet())?.toSet() ?: emptySet()
        prefs.edit()
            .putStringSet(KEY_MASTERED, mastered + topicId)
            .apply()
    }

    fun markTodayLesson(context: Context, topicId: String) {
        prefs(context).edit()
            .putString(KEY_LESSON_DATE, LocalDate.now().toString())
            .putString(KEY_LESSON_TOPIC, topicId)
            .apply()
    }

    fun levelUp(context: Context) {
        val prefs = prefs(context)
        val current = Level.fromIndex(prefs.getInt(KEY_LEVEL, 0))
        current.next?.let { prefs.edit().putInt(KEY_LEVEL, it.ordinal).apply() }
    }

    fun reminderMessage(context: Context): Pair<String, String>? {
        val state = readState(context)
        return when {
            !state.placementDone ->
                "Find your English level" to "Take the 20-question grammar test in Grammar Daily \u2014 it takes 5 minutes."
            state.allMasteredFinished ->
                "Grammar walker \u2713" to "You have mastered every lesson in the app. Time for real-world reading!"
            state.todayLessonDone ->
                "Quick review: ${state.level.code}" to "Lesson done today. Your streak: ${state.streakDays} days. Keep the flame!"
            else -> {
                val topic = GrammarCurriculum.nextTopic(state.level, state.masteredTopicIds)
                    ?: return null
                subjectLine(topic) to topic.tip
            }
        }
    }

    private fun subjectLine(topic: Topic): String = "Today: ${topic.title}"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}

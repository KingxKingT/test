package com.example.grammardaily.data

import android.content.Context
import java.time.LocalDate

data class ProgressState(
    val placementDone: Boolean,
    val level: Level,
    val masteredTopicIds: Set<String>,
    val activeBoxTopicIds: Set<String>,
    val todayLessonTopicId: String?,
    val todayLessonDone: Boolean,
    val lessonDoneTopicId: String?,
    val dueReviewTopics: List<Topic>,
    val streakDays: Int,
    val allMasteredFinished: Boolean,
    val weakTopicIds: List<String>
)

object ProgressStore {

    private const val PREFS = "grammar_daily"
    private const val KEY_PLACED = "placement_done"
    private const val KEY_LEVEL = "level_index"
    private const val KEY_BOXES = "topic_boxes"
    private const val KEY_DUE = "topic_due"
    private const val KEY_MASTERED = "mastered_topics"
    private const val KEY_LESSON_DATE = "lesson_date"
    private const val KEY_LESSON_TOPIC = "lesson_topic_id"
    private const val KEY_STREAK = "streak_days"
    private const val KEY_LAST_STUDY = "last_study_date"
    private const val KEY_WRONG = "topic_wrong"

    private const val MASTERY_BOX = 4
    private val REVIEW_INTERVALS = mapOf(1 to 1L, 2 to 3L, 3 to 7L)

    fun readState(context: Context): ProgressState {
        val prefs = prefs(context)
        val placed = prefs.getBoolean(KEY_PLACED, false)
        val level = Level.fromIndex(prefs.getInt(KEY_LEVEL, 0))
        val boxes = readBoxes(prefs)
        val due = readDue(prefs)
        val mastered = boxes.filterValues { it >= MASTERY_BOX }.keys
        val today = LocalDate.now()
        val lessonDate = prefs.getString(KEY_LESSON_DATE, null)
        val lessonTopic = prefs.getString(KEY_LESSON_TOPIC, null)
        val todayDone = lessonDate == today.toString() && lessonTopic != null
        val dueTopicIds = due.filter { (id, date) ->
            date <= today && (boxes[id] ?: 0) < MASTERY_BOX
        }.keys
        val wrong = readWrong(prefs)
        val orderedDueIds = dueTopicIds.sortedWith(
            compareByDescending<String> { wrong[it] ?: 0 }.thenBy { boxes[it] ?: 0 }
        )
        val dueTopics = orderedDueIds.mapNotNull { id -> findTopic(id) }
        val pool = GrammarCurriculum.topicsByLevel[level].orEmpty()
        val lessonTopicNext = pool.firstOrNull {
            it.id !in mastered && (boxes[it.id] ?: 0) == 0
        }
        return ProgressState(
            placementDone = placed,
            level = level,
            masteredTopicIds = mastered,
            activeBoxTopicIds = boxes.filterValues { it in 1 until MASTERY_BOX }.keys,
            todayLessonTopicId = if (!placed) null else lessonTopicNext?.id,
            todayLessonDone = todayDone,
            lessonDoneTopicId = if (todayDone) lessonTopic else null,
            dueReviewTopics = dueTopics,
            streakDays = prefs.getInt(KEY_STREAK, 0),
            allMasteredFinished = level == Level.C1 &&
                GrammarCurriculum.levelIsMastered(level, masteredList(prefs)),
            weakTopicIds = wrong.filterValues { it > 0 }
                .entries.sortedByDescending { it.value }.take(3).map { it.key }
        )
    }

    private fun masteredList(prefs: android.content.SharedPreferences): Set<String> =
        readBoxes(prefs).filterValues { it >= MASTERY_BOX }.keys

    fun markPlacement(level: Level, context: Context) {
        val p = prefs(context)
        p.edit()
            .putBoolean(KEY_PLACED, true)
            .putInt(KEY_LEVEL, level.ordinal)
            .remove(KEY_BOXES)
            .remove(KEY_DUE)
            .remove(KEY_MASTERED)
            .remove(KEY_LESSON_DATE)
            .remove(KEY_LESSON_TOPIC)
            .apply()
    }

    fun recordLesson(context: Context, topicId: String) {
        val p = prefs(context)
        val today = LocalDate.now()
        val boxes = readBoxes(p).toMutableMap()
        boxes[topicId] = 1
        val due = readDue(p).toMutableMap()
        due[topicId] = today.plusDays(1)
        val last = p.getString(KEY_LAST_STUDY, null)?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }
        val streak = when (last) {
            today -> p.getInt(KEY_STREAK, 0)
            today.minusDays(1) -> p.getInt(KEY_STREAK, 0) + 1
            else -> 1
        }
        p.edit()
            .putStringSet(KEY_BOXES, boxes.map { "${it.key}:${it.value}" }.toSet())
            .putStringSet(KEY_DUE, due.map { "${it.key}:${it.value}" }.toSet())
            .putString(KEY_LESSON_DATE, today.toString())
            .putString(KEY_LESSON_TOPIC, topicId)
            .putInt(KEY_STREAK, streak)
            .putString(KEY_LAST_STUDY, today.toString())
            .apply()
    }

    fun markReviewCorrect(context: Context, topicId: String): Boolean {
        val p = prefs(context)
        val boxes = readBoxes(p).toMutableMap()
        val newBox = (boxes[topicId] ?: 0) + 1
        boxes[topicId] = newBox
        val due = readDue(p).toMutableMap()
        val mastered = p.getStringSet(KEY_MASTERED, emptySet())?.toSet() ?: emptySet()
        var becameMastered = false
        if (newBox >= MASTERY_BOX) {
            due.remove(topicId)
            p.edit().putStringSet(KEY_MASTERED, mastered + topicId).apply()
            becameMastered = true
        } else {
            due[topicId] = LocalDate.now().plusDays(REVIEW_INTERVALS[newBox] ?: 2L)
        }
        val last = p.getString(KEY_LAST_STUDY, null)?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }
        val today = LocalDate.now()
        val streak = when (last) {
            today -> p.getInt(KEY_STREAK, 0)
            today.minusDays(1) -> p.getInt(KEY_STREAK, 0) + 1
            else -> 1
        }
        p.edit()
            .putStringSet(KEY_BOXES, boxes.map { "${it.key}:${it.value}" }.toSet())
            .putStringSet(KEY_DUE, due.map { "${it.key}:${it.value}" }.toSet())
            .putInt(KEY_STREAK, streak)
            .putString(KEY_LAST_STUDY, today.toString())
            .apply()
        return becameMastered
    }

    fun recordStreakOnly(context: Context) {
        val p = prefs(context)
        val today = LocalDate.now()
        val last = p.getString(KEY_LAST_STUDY, null)?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }
        val streak = when (last) {
            today -> p.getInt(KEY_STREAK, 0)
            today.minusDays(1) -> p.getInt(KEY_STREAK, 0) + 1
            else -> 1
        }
        p.edit()
            .putInt(KEY_STREAK, streak)
            .putString(KEY_LAST_STUDY, today.toString())
            .apply()
    }

    fun levelUp(context: Context) {
        val p = prefs(context)
        val current = Level.fromIndex(p.getInt(KEY_LEVEL, 0))
        current.next?.let { p.edit().putInt(KEY_LEVEL, it.ordinal).apply() }
    }

    fun reminderMessage(context: Context): Pair<String, String>? {
        val state = readState(context)
        return when {
            !state.placementDone ->
                "Find your English level" to "Take the 15-question mixed grammar test in Grammar Daily \u2014 it takes 5 minutes."
            state.dueReviewTopics.isNotEmpty() ->
                pickMemoryLine(state.dueReviewTopics, "Review due: ")
            state.todayLessonDone && state.todayLessonTopicId == null ->
                "Grammar Daily" to "Streak: ${state.streakDays} days. See you tomorrow for the next lesson!"
            else -> {
                val topic = state.todayLessonTopicId?.let { findTopic(it) } ?: return null
                pickMemoryLine(listOf(topic), "Today: ")
            }
        }
    }

    private fun pickMemoryLine(topics: List<Topic>, titlePrefix: String): Pair<String, String> {
        val topic = topics.first()
        val lines = mutableListOf<Pair<String, String>>()
        lines += titlePrefix + topic.title to topic.tip
        Rules.forTopic(topic.id)?.let { rule ->
            if (rule.wrong.isNotBlank() && rule.right.isNotBlank()) {
                lines += "Don\u2019t say: ${rule.wrong}" to "Say: ${rule.right} \u2014 ${rule.why}"
            }
            if (rule.pattern.isNotBlank()) {
                lines += "The pattern: ${topic.title}" to rule.pattern
            }
        }
        ChunkBank.byTopic[topic.id].orEmpty().forEach { chunk ->
            lines += "Say it as one block" to chunk
        }
        if (lines.size == 1) return lines[0]
        val slot = (System.currentTimeMillis() / 7_200_000L) % lines.size
        return lines[slot.toInt()]
    }

    private fun findTopic(id: String): Topic? =
        GrammarCurriculum.topicsByLevel.values.flatten().firstOrNull { it.id == id }

    private fun readBoxes(prefs: android.content.SharedPreferences): Map<String, Int> =
        decodeIntMap(prefs.getStringSet(KEY_BOXES, emptySet()))

    private fun readWrong(prefs: android.content.SharedPreferences): Map<String, Int> =
        decodeIntMap(prefs.getStringSet(KEY_WRONG, emptySet()))

    fun readWrongCounts(context: Context): Map<String, Int> =
        readWrong(prefs(context))

    fun recordAnswers(context: Context, results: List<ExerciseResult>) {
        if (results.isEmpty()) return
        val wrong = readWrong(prefs(context)).toMutableMap()
        results.forEach { r ->
            if (r.correct) {
                val c = wrong[r.topicId] ?: 0
                if (c > 0) wrong[r.topicId] = c - 1 else wrong.remove(r.topicId)
            } else {
                wrong[r.topicId] = (wrong[r.topicId] ?: 0) + 1
            }
        }
        prefs(context)
            .edit()
            .putStringSet(KEY_WRONG, wrong.filterValues { it > 0 }.map { "${it.key}:${it.value}" }.toSet())
            .apply()
    }

    fun findTopicById(id: String): Topic? = findTopic(id)

    private fun readDue(prefs: android.content.SharedPreferences): Map<String, LocalDate> =
        decodeStringMap(prefs.getStringSet(KEY_DUE, emptySet()))
            .mapValues { runCatching { LocalDate.parse(it.value) }.getOrDefault(LocalDate.MIN) }

    private fun decodeStringMap(raw: Set<String>?): Map<String, String> =
        raw.orEmpty().mapNotNull { entry ->
            val idx = entry.lastIndexOf(':')
            if (idx <= 0) return@mapNotNull null
            entry.substring(0, idx) to entry.substring(idx + 1)
        }.toMap()

    private fun decodeIntMap(raw: Set<String>?): Map<String, Int> =
        raw.orEmpty().mapNotNull { entry ->
            val idx = entry.lastIndexOf(':')
            if (idx <= 0) return@mapNotNull null
            val v = entry.substring(idx + 1).toIntOrNull() ?: return@mapNotNull null
            entry.substring(0, idx) to v
        }.toMap()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}

object TextScale {
    fun get(context: Context): Float =
        context.getSharedPreferences("grammar_daily", Context.MODE_PRIVATE)
            .getFloat("text_scale", 1f)

    fun set(context: Context, value: Float) {
        context.getSharedPreferences("grammar_daily", Context.MODE_PRIVATE)
            .edit()
            .putFloat("text_scale", value)
            .apply()
    }
}

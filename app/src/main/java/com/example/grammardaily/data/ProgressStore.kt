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
    val weakTopicIds: List<String>,
    val streakFreezes: Int,
    val quickRecallDone: Boolean,
    val lastStudyDate: LocalDate?
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
    private const val KEY_QUICK_DATE = "quick_recall_date"
    private const val KEY_FREEZE = "streak_freezes"
    private const val KEY_FREEZE_EARN = "freeze_earned"

    private const val MASTERY_BOX = 4
    private val REVIEW_INTERVALS = mapOf(1 to 1L, 2 to 3L, 3 to 7L, 4 to 14L)

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
        val dueTopicIds = due.filter { (_, date) -> date <= today }.keys
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
                .entries.sortedByDescending { it.value }.take(3).map { it.key },
            streakFreezes = prefs.getInt(KEY_FREEZE, 0),
            quickRecallDone = prefs.getString(KEY_QUICK_DATE, null) == today.toString(),
            lastStudyDate = prefs.getString(KEY_LAST_STUDY, null)?.let {
                runCatching { LocalDate.parse(it) }.getOrNull()
            }
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
        applyStreak(p, today)
        p.edit()
            .putStringSet(KEY_BOXES, boxes.map { "${it.key}:${it.value}" }.toSet())
            .putStringSet(KEY_DUE, due.map { "${it.key}:${it.value}" }.toSet())
            .putString(KEY_LESSON_DATE, today.toString())
            .putString(KEY_LESSON_TOPIC, topicId)
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
        if (newBox == MASTERY_BOX) {
            p.edit().putStringSet(KEY_MASTERED, mastered + topicId).apply()
            becameMastered = true
        }
        due[topicId] = LocalDate.now().plusDays(REVIEW_INTERVALS[newBox] ?: 14L)
        val today = LocalDate.now()
        applyStreak(p, today)
        p.edit()
            .putStringSet(KEY_BOXES, boxes.map { "${it.key}:${it.value}" }.toSet())
            .putStringSet(KEY_DUE, due.map { "${it.key}:${it.value}" }.toSet())
            .apply()
        return becameMastered
    }

    fun recordStreakOnly(context: Context) {
        applyStreak(prefs(context), LocalDate.now())
    }

    private fun applyStreak(p: android.content.SharedPreferences, today: LocalDate) {
        val last = p.getString(KEY_LAST_STUDY, null)?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }
        val streak = p.getInt(KEY_STREAK, 0)
        var freezes = p.getInt(KEY_FREEZE, 0)
        val newStreak = when {
            last == today -> streak
            last == today.minusDays(1) -> streak + 1
            last != null && last < today.minusDays(1) && freezes > 0 -> {
                freezes -= 1
                streak + 1
            }
            else -> 1
        }
        var earned = p.getInt(KEY_FREEZE_EARN, 0)
        if (last != today) {
            earned += 1
            if (earned >= 5 && freezes < 1) {
                freezes += 1
                earned = 0
            }
        }
        p.edit()
            .putInt(KEY_STREAK, newStreak)
            .putInt(KEY_FREEZE, freezes)
            .putInt(KEY_FREEZE_EARN, earned)
            .putString(KEY_LAST_STUDY, today.toString())
            .apply()
    }

    fun recordQuickRecall(context: Context, results: List<ExerciseResult>) {
        if (results.isEmpty()) return
        recordAnswers(context, results)
        val due = readDue(prefs(context))
        val today = LocalDate.now()
        results.filter { it.correct }.map { it.topicId }.distinct().forEach { id ->
            if ((due[id] ?: LocalDate.MAX) <= today) {
                markReviewCorrect(context, id)
            }
        }
        val p = prefs(context)
        p.edit().putString(KEY_QUICK_DATE, today.toString()).apply()
        applyStreak(p, today)
    }

    fun levelUp(context: Context) {
        val p = prefs(context)
        val current = Level.fromIndex(p.getInt(KEY_LEVEL, 0))
        current.next?.let { p.edit().putInt(KEY_LEVEL, it.ordinal).apply() }
    }

    fun reminderMessage(context: Context): Pair<String, String>? {
        val state = readState(context)
        val longAgo = state.lastStudyDate != null &&
            state.lastStudyDate < LocalDate.now().minusDays(1)
        return when {
            !state.placementDone ->
                "Find your English level" to "Take the 15-question mixed grammar test in Grammar Daily \u2014 it takes 5 minutes."
            longAgo && state.streakDays > 0 ->
                "Your streak is waiting \uD83D\uDD25" to "${state.streakDays} days of English \u2014 one 1-minute quick recall in Grammar Daily keeps it alive."
            longAgo ->
                "One small step today" to "A 1-minute quick recall in Grammar Daily is enough \u2014 little and often wins."
            state.dueReviewTopics.isNotEmpty() ->
                pickMemoryLine(state.dueReviewTopics, "Review due: ")
            state.todayLessonDone -> {
                val nextTopic = state.todayLessonTopicId?.let { findTopic(it) }
                if (nextTopic != null) {
                    "Well done today!" to "Today's lesson is finished. ${nextTopic.title} is waiting for tomorrow \u2014 or practice it again now if you like."
                } else {
                    "Grammar Daily" to "Streak: ${state.streakDays} days. See you tomorrow for the next lesson!"
                }
            }
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

    private const val KEY_TRANSLATE_LANG = "translate_lang"

    fun translateLang(context: Context): String? =
        prefs(context).getString(KEY_TRANSLATE_LANG, null)

    fun setTranslateLang(context: Context, code: String?) {
        val editor = prefs(context).edit()
        if (code == null) editor.remove(KEY_TRANSLATE_LANG) else editor.putString(KEY_TRANSLATE_LANG, code)
        editor.apply()
    }

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

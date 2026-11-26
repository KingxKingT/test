package com.example.grammardaily.data

import android.content.Context
import kotlin.random.Random

object VerbStore {

    private const val PREFS = "verb_widget"
    private const val KEY_VERBS = "active_verbs"
    private const val KEY_MASTERED = "mastered_verbs"
    private const val KEY_KNOWN = "known_cards"
    private const val KEY_SHOW_AGAIN = "show_again_cards"
    private const val KEY_TIMES = "times_per_day"
    private const val KEY_QUIET = "quiet_hours"
    private const val KEY_CARD = "current_card"
    private const val KEY_LAST_ROT = "last_rotation_ms"

    private val DEFAULT_VERBS = listOf("go", "take", "have")

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun activeVerbs(context: Context): List<String> {
        val stored = prefs(context).getStringSet(KEY_VERBS, emptySet()).orEmpty()
        if (stored.isNotEmpty()) return stored.sorted()
        prefs(context).edit().putStringSet(KEY_VERBS, DEFAULT_VERBS.toSet()).apply()
        return DEFAULT_VERBS
    }

    fun setActiveVerbs(context: Context, verbs: List<String>) {
        prefs(context).edit()
            .putStringSet(KEY_VERBS, verbs.take(3).map { it.lowercase().trim() }.toSet())
            .apply()
    }

    fun masteredVerbs(context: Context): List<String> =
        prefs(context).getStringSet(KEY_MASTERED, emptySet()).orEmpty().sorted()

    fun knownCards(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_KNOWN, emptySet()).orEmpty()

    fun showAgainCards(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_SHOW_AGAIN, emptySet()).orEmpty()

    fun timesPerDay(context: Context): Int = prefs(context).getInt(KEY_TIMES, 3)

    fun setTimesPerDay(context: Context, times: Int) {
        prefs(context).edit().putInt(KEY_TIMES, times.coerceIn(1, 6)).apply()
    }

    fun quietHours(context: Context): Int = prefs(context).getInt(KEY_QUIET, 2)

    fun setQuietHours(context: Context, mode: Int) {
        prefs(context).edit().putInt(KEY_QUIET, mode).apply()
    }

    fun quietWindow(mode: Int): Pair<Int, Int>? = when (mode) {
        1 -> Pair(22, 7)
        2 -> Pair(23, 8)
        3 -> Pair(0, 6)
        else -> null
    }

    fun currentCard(context: Context): String? =
        prefs(context).getString(KEY_CARD, null)

    fun setCurrentCard(context: Context, descriptor: String) {
        prefs(context).edit().putString(KEY_CARD, descriptor).apply()
    }

    fun lastRotation(context: Context): Long = prefs(context).getLong(KEY_LAST_ROT, 0L)

    fun setLastRotation(context: Context, ms: Long) {
        prefs(context).edit().putLong(KEY_LAST_ROT, ms).apply()
    }

    fun markKnown(context: Context, descriptor: String) {
        val known = knownCards(context) + descriptor
        val again = showAgainCards(context) - descriptor
        val verb = descriptor.substringBefore("|")
        val p = prefs(context).edit()
        p.putStringSet(KEY_KNOWN, known)
        p.putStringSet(KEY_SHOW_AGAIN, again)
        if (knownFraction(known, verb) >= 0.8) {
            p.putStringSet(KEY_MASTERED, masteredVerbs(context).toSet() + verb)
        }
        p.apply()
    }

    fun markShowAgain(context: Context, descriptor: String) {
        prefs(context).edit()
            .putStringSet(KEY_SHOW_AGAIN, showAgainCards(context) + descriptor)
            .apply()
    }

    fun knownFraction(known: Set<String>, verb: String): Double {
        val total = VerbBank.TENSE_COUNT * 3
        val count = known.count { it.startsWith("$verb|") }
        return count.toDouble() / total
    }

    fun pickNextCard(context: Context, rng: Random = Random.Default): String {
        val verbs = activeVerbs(context)
        if (verbs.isEmpty()) return "go|0|0"
        val again = showAgainCards(context).filter { desc ->
            desc.substringBefore("|") in verbs && desc != currentCard(context)
        }
        if (again.isNotEmpty() && rng.nextInt(100) < 50) {
            return again.random(rng)
        }
        val verb = if (masteredVerbs(context).size in 1..2 && rng.nextInt(100) < 20) {
            masteredVerbs(context).random(rng)
        } else {
            verbs.random(rng)
        }
        val tense = rng.nextInt(VerbBank.TENSE_COUNT)
        val form = rng.nextInt(3)
        return "$verb|$tense|$form"
    }

    fun rotate(context: Context) {
        setCurrentCard(context, pickNextCard(context))
        setLastRotation(context, System.currentTimeMillis())
    }
}

package com.example.grammardaily.data

/**
 * Deterministic, no-AI feedback: it compares the learner's answer with the
 * correct one word by word and says exactly what is wrong, in plain words.
 * Because it is pure logic it can never hallucinate \u2014 the AI is only
 * consulted for cases this cannot settle.
 */
object Feedback {

    private fun tokens(s: String): List<String> =
        ExerciseEngine.normalize(s).split(" ").filter { it.isNotBlank() }

    /**
     * What exactly is wrong with this answer, in one short line, or null when
     * nothing is provably wrong (the AI then decides).
     */
    fun diagnose(exercise: Exercise, given: String): String? {
        val typed = given.trim()
        if (typed.isBlank()) return null
        val gTok = tokens(typed)
        val aTok = tokens(exercise.answer)
        return when (exercise.kind) {
            ExKind.TYPE -> {
                val variants = (exercise.accepted.takeIf { it.isNotEmpty() } ?: listOf(exercise.answer))
                    .map { tokens(it) }
                    .filter { it.isNotEmpty() }
                if (variants.isNotEmpty() &&
                    variants.all { v -> v.any { kt -> gTok.none { g -> ExerciseEngine.closeEnough(g, kt) } } }
                ) {
                    "The blank needs different word(s) \u2014 re-read the pattern above."
                } else {
                    null
                }
            }
            ExKind.FIX -> {
                val wrongTok = tokens(exercise.sentence).toSet()
                val must = aTok.filter { it !in wrongTok && it.length >= 2 }
                if (must.isNotEmpty() &&
                    must.all { mt -> gTok.none { g -> ExerciseEngine.closeEnough(g, mt) } }
                ) {
                    "Your answer still has the original mistake \u2014 find the word that must change."
                } else {
                    null
                }
            }
            ExKind.LISTEN -> {
                if (aTok.isNotEmpty() &&
                    aTok.count { at -> gTok.any { g -> ExerciseEngine.closeEnough(g, at) } } < aTok.size * 0.6
                ) {
                    "Some words are missing \u2014 play it once more and count the words."
                } else {
                    null
                }
            }
            ExKind.REORDER -> {
                if (gTok.size == aTok.size && gTok.sorted() == aTok.sorted() && gTok != aTok) {
                    "All the words are right \u2014 but the order is not. Where does the verb go?"
                } else {
                    null
                }
            }
            ExKind.WRITE -> when {
                gTok.size < 4 -> "Too short \u2014 even a small full sentence is enough."
                gTok == tokens(exercise.answer) ->
                    "That is the example word for word \u2014 write your own sentence."
                else -> null
            }
            ExKind.TRANSLATE -> {
                if (aTok.isNotEmpty() && gTok.size * 2 < aTok.size) {
                    "That looks too short for the whole sentence."
                } else {
                    null
                }
            }
            ExKind.MCQ -> null
        }
    }

    /**
     * True only for clear, provably wrong typed answers (TYPE / FIX / LISTEN).
     * These are settled locally so the small on-device AI never gets the final
     * say on an answer that is missing the tested word.
     */
    fun deterministicallyWrong(exercise: Exercise, given: String): Boolean {
        return when (exercise.kind) {
            ExKind.TYPE, ExKind.FIX, ExKind.LISTEN -> diagnose(exercise, given) != null
            else -> false
        }
    }
}

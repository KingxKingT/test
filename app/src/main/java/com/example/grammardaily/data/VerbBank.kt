package com.example.grammardaily.data

import kotlin.random.Random

data class VerbForms(
    val base: String,
    val v2: String,
    val v3: String,
    val ing: String,
    val sForm: String
) {
    fun display(): String = "$base / $v2 / $v3 / $ing"
}

data class VerbEvent(
    val verb: String,
    val tense: Int,
    val form: Int,
    val main: String,
    val alt: String,
    val targetForm: String
) {
    val tenseName: String get() = VerbBank.tenseName(tense)
    val useLine: String get() = VerbBank.useLine(tense)
    val formsLine: String get() = VerbBank.forms(verb).display()
    fun descriptor(): String = "$verb|$tense|$form"
}

object VerbBank {

    const val TENSE_COUNT = 8

    private val irregular: Map<String, Pair<String, String>> = mapOf(
        "go" to Pair("went", "gone"), "take" to Pair("took", "taken"),
        "make" to Pair("made", "made"), "get" to Pair("got", "gotten"),
        "have" to Pair("had", "had"), "do" to Pair("did", "done"),
        "say" to Pair("said", "said"), "come" to Pair("came", "come"),
        "see" to Pair("saw", "seen"), "give" to Pair("gave", "given"),
        "find" to Pair("found", "found"), "know" to Pair("knew", "known"),
        "think" to Pair("thought", "thought"), "tell" to Pair("told", "told"),
        "become" to Pair("became", "become"), "leave" to Pair("left", "left"),
        "feel" to Pair("felt", "felt"), "put" to Pair("put", "put"),
        "bring" to Pair("brought", "brought"), "run" to Pair("ran", "run"),
        "write" to Pair("wrote", "written"), "sit" to Pair("sat", "sat"),
        "stand" to Pair("stood", "stood"), "lose" to Pair("lost", "lost"),
        "pay" to Pair("paid", "paid"), "meet" to Pair("met", "met"),
        "keep" to Pair("kept", "kept"), "sleep" to Pair("slept", "slept"),
        "speak" to Pair("spoke", "spoken"), "read" to Pair("read", "read"),
        "buy" to Pair("bought", "bought"), "send" to Pair("sent", "sent"),
        "build" to Pair("built", "built"), "understand" to Pair("understood", "understood"),
        "catch" to Pair("caught", "caught"), "choose" to Pair("chose", "chosen"),
        "eat" to Pair("ate", "eaten"), "drink" to Pair("drank", "drunk"),
        "drive" to Pair("drove", "driven"), "break" to Pair("broke", "broken"),
        "forget" to Pair("forgot", "forgotten"), "wear" to Pair("wore", "worn"),
        "teach" to Pair("taught", "taught"), "win" to Pair("won", "won"),
        "begin" to Pair("began", "begun"), "hold" to Pair("held", "held"),
        "spend" to Pair("spent", "spent"), "rise" to Pair("rose", "risen"),
        "fall" to Pair("fell", "fallen"), "lead" to Pair("led", "led"),
        "hear" to Pair("heard", "heard"), "swim" to Pair("swam", "swum"),
        "stop" to Pair("stopped", "stopped"), "study" to Pair("studied", "studied")
    )

    private val ingOverride = mapOf(
        "have" to "having", "make" to "making", "take" to "taking",
        "give" to "giving", "come" to "coming", "run" to "running",
        "get" to "getting", "put" to "putting", "sit" to "sitting",
        "swim" to "swimming", "stop" to "stopping", "begin" to "beginning",
        "lose" to "losing", "write" to "writing", "drive" to "driving",
        "leave" to "leaving", "become" to "becoming", "choose" to "choosing",
        "rise" to "rising", "see" to "seeing"
    )

    val contexts: Map<String, List<String>> = mapOf(
        "go" to listOf("to school", "home early", "for a walk"),
        "take" to listOf("the bus", "a photo", "an umbrella"),
        "make" to listOf("dinner", "a cake", "a mistake"),
        "get" to listOf("tired", "a gift", "home late"),
        "have" to listOf("breakfast", "a shower", "a rest"),
        "do" to listOf("the dishes", "your homework", "some sport"),
        "say" to listOf("hello", "nothing", "sorry"),
        "come" to listOf("home", "to the party", "back soon"),
        "see" to listOf("the doctor", "a film", "your friends"),
        "give" to listOf("me a hand", "a present", "an answer"),
        "find" to listOf("your keys", "a job", "time to rest"),
        "know" to listOf("the answer", "his name", "the way"),
        "think" to listOf("about it", "of a plan", "twice"),
        "bring" to listOf("some water", "your book", "a friend"),
        "keep" to listOf("your phone", "a secret", "trying"),
        "run" to listOf("every morning", "a marathon", "a small shop")
    )

    val defaultContexts = listOf("every day", "at home", "after work")

    private val subjects = listOf(
        Triple("I", false, "I"),
        Triple("you", false, "you"),
        Triple("she", true, "she"),
        Triple("he", true, "he"),
        Triple("we", false, "we"),
        Triple("they", false, "they"),
        Triple("my friend", true, "she"),
        Triple("the kids", false, "they")
    )

    private val tenseNames = listOf(
        "Present simple", "Present continuous", "Past simple", "Past continuous",
        "Present perfect", "Past perfect", "Future (will)", "Conditional (would)"
    )

    private val useLines = listOf(
        "Habits and facts that are always true.",
        "It is happening right now or around now.",
        "Finished at a known time in the past.",
        "Something was going on \u2014 background or interrupted.",
        "The past connects to now: the result matters today.",
        "An action that happened before another past action.",
        "Decided now, a promise, or a prediction.",
        "Imagined, not real \u2014 or depends on a condition."
    )

    fun tenseName(i: Int): String = tenseNames[i.coerceIn(0, tenseNames.lastIndex)]
    fun useLine(i: Int): String = useLines[i.coerceIn(0, useLines.lastIndex)]

    fun forms(verbRaw: String): VerbForms {
        val base = verbRaw.lowercase().trim()
        if (irregular.containsKey(base)) {
            val (v2, v3) = irregular[base]!!
            return VerbForms(base, v2, v3, ingOverride[base] ?: ing(base), sForm(base))
        }
        return VerbForms(base, v2(base), v2(base), ingOverride[base] ?: ing(base), sForm(base))
    }

    private fun isVowel(c: Char) = c in "aeiou"

    private fun v2(base: String): String = when {
        base.endsWith("e") -> base + "d"
        base.length > 2 && !isVowel(base.last()) && isVowel(base[base.length - 2]) &&
            !isVowel(base[base.length - 3]) && base.last() !in "wxy" -> base + base.last() + "ed"
        base.length > 1 && base.last() == 'y' && !isVowel(base[base.length - 2]) ->
            base.dropLast(1) + "ied"
        else -> base + "ed"
    }

    private fun ing(base: String): String = when {
        base.endsWith("e") && base != "be" -> base.dropLast(1) + "ing"
        else -> base + "ing"
    }

    private fun sForm(base: String): String = when {
        base == "have" -> "has"
        base.endsWith("s") || base.endsWith("x") || base.endsWith("z") ||
            base.endsWith("ch") || base.endsWith("sh") -> base + "es"
        base.endsWith("y") && base.length > 1 && !isVowel(base[base.length - 2]) ->
            base.dropLast(1) + "ies"
        else -> base + "s"
    }

    fun contextList(verb: String): List<String> =
        contexts[verb.lowercase().trim()] ?: defaultContexts

    fun event(
        verb: String,
        tense: Int,
        form: Int,
        rng: Random = Random.Default,
        subjectIdx: Int = rng.nextInt(subjects.size),
        contextIdx: Int = rng.nextInt(contextList(verb).size)
    ): VerbEvent {
        val t = tense.coerceIn(0, TENSE_COUNT - 1)
        val f = form.coerceIn(0, 2)
        val f0 = forms(verb)
        val ctxs = contextList(verb)
        val ctx = ctxs[contextIdx % ctxs.size]
        val (subject, third, _) = subjects[subjectIdx % subjects.size]

        val f1 = forms(verb)
        if (t == 7) {
            val altCtx = ctxs[(contextIdx + 1) % ctxs.size]
            val returnMain = when (f) {
                2 -> "Would you ${f1.base} $altCtx if you had more time?"
                else -> {
                    val neg = f == 1
                    val echo = subjects[subjectIdx % subjects.size].third
                    val inner = "would" + if (neg) "n\u2019t" else ""
                    subject.replaceFirstChar { it.uppercase() } + " " + inner + " " +
                        f1.base + " " + altCtx + " if " + echo + " had the time?"
                }
            }
            val targetForm = if (f == 2) f1.base else f1.base
            val altMain = "If I were you, I " + (if (f == 1) "would" else "wouldn\u2019t") +
                " " + f1.base + " " + altCtx + "."
            return VerbEvent(verb, t, f, returnMain, altMain, targetForm)
        }

        val main = when (t) {
            0 -> when (f) {
                0 -> "${subject} ${if (third) f0.sForm else f0.base} ${ctx}"
                1 -> "${subject} ${if (third) "doesn\u2019t" else "don\u2019t"} ${f0.base} ${ctx}"
                else -> "${if (third) "Does" else "Do"} ${subject} ${f0.base} ${ctx}?"
            }
            1 -> {
                val be = if (subject == "I") "am" else if (third) "is" else "are"
                val beNeg = if (subject == "I") "am not" else if (third) "isn\u2019t" else "aren\u2019t"
                when (f) {
                    0 -> "${subject} ${be} ${f0.ing} ${ctx}"
                    1 -> "${subject} ${beNeg} ${f0.ing} ${ctx}"
                    else -> "${be.replaceFirstChar { it.uppercase() }} ${subject} ${f0.ing} ${ctx}?"
                }
            }
            2 -> when (f) {
                0 -> "${subject} ${f0.v2} ${ctx}"
                1 -> "${subject} didn\u2019t ${f0.base} ${ctx}"
                else -> "Did ${subject} ${f0.base} ${ctx}?"
            }
            3 -> {
                val was = subject == "I" || third
                val wasWord = if (was) "was" else "were"
                val wasNeg = if (was) "wasn\u2019t" else "weren\u2019t"
                when (f) {
                    0 -> "${subject} ${wasWord} ${f0.ing} ${ctx}"
                    1 -> "${subject} ${wasNeg} ${f0.ing} ${ctx}"
                    else -> "${if (was) "Was" else "Were"} ${subject} ${f0.ing} ${ctx}?"
                }
            }
            4 -> when (f) {
                0 -> "${subject} ${if (third) "has" else "have"} ${f0.v3} ${ctx}"
                1 -> "${subject} ${if (third) "hasn\u2019t" else "haven\u2019t"} ${f0.v3} ${ctx}"
                else -> "${if (third) "Has" else "Have"} ${subject} ${f0.v3} ${ctx}?"
            }
            5 -> when (f) {
                0 -> "${subject} had ${f0.v3} ${ctx}"
                1 -> "${subject} hadn\u2019t ${f0.v3} ${ctx}"
                else -> "Had ${subject} ${f0.v3} ${ctx}?"
            }
            else -> when (f) {
                0 -> "${subject} will ${f0.base} ${ctx}"
                1 -> "${subject} won\u2019t ${f0.base} ${ctx}"
                else -> "Will ${subject} ${f0.base} ${ctx}?"
            }
        }

        val targetForm = when (t) {
            1, 3 -> f0.ing
            4, 5 -> f0.v3
            2 -> if (f == 0) f0.v2 else f0.base
            else -> if (f == 0 && t == 0) (if (third) f0.sForm else f0.base) else f0.base
        }

        val altCtx = ctxs[(contextIdx + 1) % ctxs.size]
        val altIdx = subjectIdx + 3
        val (altSubject, altThird, _) = subjects[altIdx % subjects.size]
        val altMain = when (t) {
            0 -> "For example: ${altSubject} ${if (altThird) f0.sForm else f0.base} ${altCtx}."
            1 -> "For example: ${altSubject} ${if (altSubject == "I") "am" else if (altThird) "is" else "are"} ${f0.ing} ${altCtx}."
            2 -> "For example: ${altSubject} ${f0.v2} ${altCtx} yesterday."
            3 -> "For example: ${altSubject} ${if (altSubject == "I" || altThird) "was" else "were"} ${f0.ing} ${altCtx} all evening."
            4 -> "For example: ${altSubject} ${if (altThird) "has" else "have"} ${f0.v3} ${altCtx} before."
            5 -> "For example: ${altSubject} had ${f0.v3} ${altCtx} before that."
            6 -> "For example: ${altSubject} will ${f0.base} ${altCtx} tomorrow."
            else -> "For example: ${altSubject} would ${f0.base} ${altCtx} if ${subjects[altIdx % subjects.size].third} had time."
        }

        return VerbEvent(verb, t, f, main, altMain, targetForm)
    }

    fun suggestions(level: Level): List<String> = when (level) {
        Level.A1, Level.A2 -> listOf("go", "take", "make", "get", "have", "do")
        Level.B1 -> listOf("say", "come", "see", "give", "find", "know")
        Level.B2 -> listOf("think", "bring", "keep", "run", "put", "tell")
        Level.C1 -> listOf("lead", "hold", "spend", "catch", "break", "wear")
    }

}

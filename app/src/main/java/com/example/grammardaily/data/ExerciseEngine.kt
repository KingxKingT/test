package com.example.grammardaily.data

object ExerciseEngine {

    fun normalize(raw: String): String = raw
        .lowercase()
        .replace('\u2019', '\'')
        .replace("\u00a0", " ")
        .replace(Regex("[.,!?;:\u201c\u201d\"]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()

    fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + if (a[i - 1] == b[j - 1]) 0 else 1
                )
            }
        }
        return dp[a.length][b.length]
    }

    fun closeEnough(given: String, target: String): Boolean {
        val g = normalize(given)
        val t = normalize(target)
        if (g == t) return true
        return g.length >= 5 && levenshtein(g, t) <= 1
    }

    fun mcqPool(topicId: String): List<QuizQuestion> = QuestionBank.byTopic(topicId)

    fun mcqExercise(q: QuizQuestion) = Exercise(
        topicId = q.topicId,
        kind = ExKind.MCQ,
        prompt = q.question,
        options = q.options,
        correctIndex = q.correctIndex,
        explanation = q.explanation,
        hint = q.hint
    )

    fun typePool(topicId: String): List<Exercise> =
        QuestionBank.byTopic(topicId)
            .filter { it.question.contains("___") }
            .map { q ->
                Exercise(
                    topicId = topicId,
                    kind = ExKind.TYPE,
                    prompt = "Type the missing word or words",
                    sentence = q.question,
                    accepted = listOf(q.options[q.correctIndex]),
                    explanation = q.explanation,
                    hint = q.hint
                )
            }

    fun reorderSentence(topicId: String): String? = ExerciseBank.reorder[topicId]

    fun reorderExercise(topicId: String): Exercise? {
        val sentence = reorderSentence(topicId) ?: return null
        val words = sentence.split(" ")
        if (words.size < 3) return null
        var scrambled = words.shuffled()
        var guard = 0
        while (scrambled.joinToString(" ") == sentence && guard < 10) {
            scrambled = words.shuffled()
            guard++
        }
        return Exercise(
            topicId = topicId,
            kind = ExKind.REORDER,
            prompt = "Tap the words to build the sentence",
            words = scrambled,
            answer = sentence,
            explanation = "",
            hint = ""
        )
    }

    fun fixExercise(topicId: String): Exercise? {
        val pair = ExerciseBank.fix[topicId] ?: return null
        return Exercise(
            topicId = topicId,
            kind = ExKind.FIX,
            prompt = "Fix the mistake \u2014 type the whole sentence again, corrected",
            sentence = pair.first,
            accepted = listOf(pair.second),
            explanation = "",
            hint = ""
        )
    }

    fun listenPool(topicId: String): List<Exercise> =
        listOfNotNull(reorderSentence(topicId)).map { sentence ->
            Exercise(
                topicId = topicId,
                kind = ExKind.LISTEN,
                prompt = "Listen and type what you hear",
                sentence = sentence,
                accepted = listOf(sentence),
                explanation = "",
                hint = "This sentence practices the lesson\u2019s pattern."
            )
        }

    fun tryExercise(topicId: String): Exercise? =
        typePool(topicId).firstOrNull()
            ?: reorderExercise(topicId)
            ?: mcqPool(topicId).firstOrNull()?.let { mcqExercise(it) }

    fun practiceSet(topicId: String): List<Exercise> {
        val mcqs = mcqPool(topicId).shuffled()
        val types = typePool(topicId).shuffled()
        val rest = buildList {
            addAll(types.take(2))
            reorderExercise(topicId)?.let { add(it) }
            fixExercise(topicId)?.let { add(it) }
        }
        val fillers = (types.drop(2) + listenPool(topicId)).shuffled().toMutableList()
        val result = buildList {
            mcqs.firstOrNull()?.let { add(mcqExercise(it)) }
            addAll(rest)
            while (size < 5 && fillers.isNotEmpty()) add(fillers.removeAt(0))
            if (size < 5) mcqs.drop(1).take(5 - size).forEach { add(mcqExercise(it)) }
        }.toMutableList()
        return result.shuffled()
    }

    fun reviewExercise(topicId: String, attempt: Int): Exercise? {
        val cycle = listOf(ExKind.TYPE, ExKind.REORDER, ExKind.FIX, ExKind.LISTEN, ExKind.MCQ)
        val kind = cycle[attempt % cycle.size]
        val primary: Exercise? = when (kind) {
            ExKind.TYPE -> typePool(topicId).randomOrNull()
            ExKind.REORDER -> reorderExercise(topicId)
            ExKind.FIX -> fixExercise(topicId)
            ExKind.LISTEN -> listenPool(topicId).randomOrNull()
            ExKind.MCQ -> mcqPool(topicId).randomOrNull()?.let { mcqExercise(it) }
        }
        if (primary != null) return primary
        return listOf(
            typePool(topicId).randomOrNull(),
            reorderExercise(topicId),
            fixExercise(topicId),
            listenPool(topicId).randomOrNull(),
            mcqPool(topicId).randomOrNull()?.let { mcqExercise(it) }
        ).filterNotNull().randomOrNull()
    }

    fun placementTest(): List<Exercise> {
        val test = mutableListOf<Exercise>()
        val kindOrder = listOf(ExKind.TYPE, ExKind.REORDER, ExKind.FIX, ExKind.MCQ)
        Level.entries.forEachIndexed { band, level ->
            val topics = GrammarCurriculum.topicsByLevel[level].orEmpty().shuffled().take(3)
            topics.forEachIndexed { j, topic ->
                val wanted = kindOrder[(band + j) % kindOrder.size]
                val mcqHardest = mcqPool(topic.id).lastOrNull()?.let { mcqExercise(it) }
                val exercise: Exercise? = when (wanted) {
                    ExKind.TYPE -> typePool(topic.id).lastOrNull()
                    ExKind.REORDER -> reorderExercise(topic.id)
                    ExKind.FIX -> fixExercise(topic.id)
                    ExKind.MCQ -> mcqHardest
                    ExKind.LISTEN -> null
                } ?: typePool(topic.id).lastOrNull()
                    ?: reorderExercise(topic.id)
                    ?: fixExercise(topic.id)
                    ?: mcqHardest
                if (exercise != null) test.add(exercise)
            }
        }
        return test
    }

    fun levelFromResults(exercises: List<Exercise>, results: List<ExerciseResult>): Level {
        var highest = Level.A1
        for ((bandIndex, band) in exercises.chunked(3).withIndex()) {
            val bandResults = results.drop(bandIndex * 3).take(3)
            val correct = bandResults.count { it.correct }
            if (correct < 2) break
            highest = Level.fromIndex(bandIndex)
        }
        return highest
    }

    fun topicScores(exercises: List<Exercise>, results: List<ExerciseResult>): Map<String, Pair<Int, Int>> {
        val scores = linkedMapOf<String, Pair<Int, Int>>()
        exercises.forEachIndexed { i, ex ->
            val correct = results.getOrNull(i)?.correct == true
            val (c, t) = scores[ex.topicId] ?: Pair(0, 0)
            scores[ex.topicId] = Pair(c + if (correct) 1 else 0, t + 1)
        }
        return scores
    }

    fun studyNext(exercises: List<Exercise>, results: List<ExerciseResult>, max: Int = 5): List<String> {
        val missed = exercises.zip(results).filter { !it.second.correct }.map { it.first.topicId }
        val order = Level.entries.flatMap { GrammarCurriculum.topicsByLevel[it].orEmpty().map { t -> t.id } }
        return missed.distinct().sortedBy { order.indexOf(it) }.take(max)
    }
}

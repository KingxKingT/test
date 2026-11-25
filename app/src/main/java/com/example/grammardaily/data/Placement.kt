package com.example.grammardaily.data

object Placement {
    const val BAND_SIZE = 5
    const val PASS_PER_BAND = 3

    fun testQuestions(): List<QuizQuestion> =
        Level.entries.flatMap { level ->
            val topics = GrammarCurriculum.topicsByLevel[level].orEmpty()
            val firstOfEach = topics.mapNotNull { topic ->
                QuestionBank.byTopic(topic.id).firstOrNull()
            }
            val hardest = topics.firstNotNullOfOrNull { topic ->
                QuestionBank.byTopic(topic.id).lastOrNull()
            }
            (firstOfEach.take(BAND_SIZE - 1) + listOfNotNull(hardest)).take(BAND_SIZE)
        }

    fun levelFromAnswers(correctByIndex: List<Boolean>): Level {
        var highest = Level.A1
        for ((bandIndex, band) in correctByIndex.chunked(BAND_SIZE).withIndex()) {
            val passed = band.count { it } >= PASS_PER_BAND
            if (!passed) break
            highest = Level.fromIndex(bandIndex)
        }
        return highest
    }
}

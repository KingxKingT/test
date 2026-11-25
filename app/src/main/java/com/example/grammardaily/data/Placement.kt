package com.example.grammardaily.data

object Placement {
    const val TOTAL_QUESTIONS = 20

    fun testQuestions(): List<QuizQuestion> =
        Level.entries.flatMap { level ->
            GrammarCurriculum.topicsByLevel[level].orEmpty().mapNotNull { topic ->
                GrammarCurriculum.questionsForTopic(topic.id).firstOrNull()
            }
        }

    fun levelForScore(correct: Int): Level = when {
        correct <= 5 -> Level.A1
        correct <= 9 -> Level.A2
        correct <= 13 -> Level.B1
        correct <= 17 -> Level.B2
        else -> Level.C1
    }
}

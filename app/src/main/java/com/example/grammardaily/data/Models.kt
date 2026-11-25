package com.example.grammardaily.data

enum class Level(val code: String, val title: String) {
    A1("A1", "Beginner"),
    A2("A2", "Elementary"),
    B1("B1", "Intermediate"),
    B2("B2", "Upper-Intermediate"),
    C1("C1", "Advanced");

    val next: Level?
        get() = when (this) {
            A1 -> A2
            A2 -> B1
            B1 -> B2
            B2 -> C1
            C1 -> null
        }

    companion object {
        fun fromIndex(index: Int): Level = entries[index.coerceIn(0, entries.lastIndex)]
    }
}

data class Topic(
    val id: String,
    val title: String,
    val explanation: String,
    val examples: List<String>,
    val tip: String
)

data class QuizQuestion(
    val topicId: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val hint: String = ""
)

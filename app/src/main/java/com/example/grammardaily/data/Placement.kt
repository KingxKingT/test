package com.example.grammardaily.data

data class PlacementResult(
    val level: Level,
    val scores: List<Triple<Topic, Int, Int>>,
    val studyNext: List<Topic>
)

object Placement {

    fun mixedTest(): List<Exercise> = ExerciseEngine.placementTest()

    fun evaluate(exercises: List<Exercise>, results: List<ExerciseResult>): PlacementResult {
        val level = ExerciseEngine.levelFromResults(exercises, results)
        val order = Level.entries.flatMap { lvl ->
            GrammarCurriculum.topicsByLevel[lvl].orEmpty().map { it.id }
        }
        val scores = ExerciseEngine.topicScores(exercises, results)
            .entries
            .sortedBy { order.indexOf(it.key) }
            .mapNotNull { (id, ct) ->
                ProgressStore.findTopicById(id)?.let { Triple(it, ct.first, ct.second) }
            }
        val next = ExerciseEngine.studyNext(exercises, results)
            .mapNotNull { ProgressStore.findTopicById(it) }
        return PlacementResult(level, scores, next)
    }
}

package com.example.grammardaily.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.grammardaily.data.GrammarCurriculum
import com.example.grammardaily.data.Level
import com.example.grammardaily.data.Placement
import com.example.grammardaily.data.ProgressStore
import com.example.grammardaily.data.ProgressState
import com.example.grammardaily.data.QuizQuestion
import com.example.grammardaily.data.Topic

sealed interface AppScreen {
    data object Home : AppScreen
    data object Placement : AppScreen
    data class Lesson(val topic: Topic) : AppScreen
    data class TopicPractice(val topic: Topic, val questions: List<QuizQuestion>) : AppScreen
    data class LevelUpTest(val questions: List<QuizQuestion>) : AppScreen
}

private data class ShownOption(val originalIndex: Int, val text: String, val isCorrect: Boolean)

@Composable
private fun shuffledOptions(question: QuizQuestion): List<ShownOption> {
    val indexed = question.options.mapIndexed { index, text ->
        ShownOption(index, text, index == question.correctIndex)
    }
    return remember(question) { indexed.shuffled() }
}

@Composable
fun HomeScreen(
    refresh: Int,
    onStartPlacement: () -> Unit,
    onOpenLesson: (Topic) -> Unit,
    onStartLevelUp: (List<QuizQuestion>) -> Unit,
    onExtraPractice: (Topic) -> Unit
) {
    val context = LocalContext.current
    var state by remember { mutableStateOf<ProgressState?>(null) }
    LaunchedEffect(refresh) {
        state = ProgressStore.readState(context)
    }

    val current = state ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Grammar Daily",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (!current.placementDone) {
            Text(
                text = "First, a full grammar test: ${Placement.TOTAL_QUESTIONS} questions from easy to advanced. Your score decides where your daily lessons start.",
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = onStartPlacement, modifier = Modifier.fillMaxWidth()) {
                Text("Start the grammar test")
            }
            return@Column
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Your level: ${current.level.code} \u00b7 ${current.level.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (current.streakDays > 0) "Streak: ${current.streakDays} day(s) \ud83d\udd25"
                    else "Study today to start a streak",
                    style = MaterialTheme.typography.bodyMedium
                )
                val levelTopics = GrammarCurriculum.topicsByLevel[current.level].orEmpty()
                val masteredCount = levelTopics.count { it.id in current.masteredTopicIds }
                LinearProgressIndicator(
                    progress = {
                        if (levelTopics.isEmpty()) 0f else masteredCount.toFloat() / levelTopics.size
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$masteredCount of ${levelTopics.size} lessons mastered at this level",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        val levelMastered = GrammarCurriculum.levelIsMastered(current.level, current.masteredTopicIds)
        when {
            current.allMasteredFinished -> {
                Text(
                    text = "You have walked the whole path: every lesson from beginner to advanced is mastered. Impressive!",
                    style = MaterialTheme.typography.bodyLarge
                )
                OutlinedButton(
                    onClick = {
                        GrammarCurriculum.topicsByLevel[Level.A1]?.firstOrNull()?.let(onExtraPractice)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Keep practicing")
                }
            }

            levelMastered -> {
                Text(
                    text = "All lessons at ${current.level.code} are mastered. Prove it with the level-up test!",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = {
                        current.level.next?.let { next ->
                            onStartLevelUp(GrammarCurriculum.levelUpQuestions(next).take(6))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take the level-up test")
                }
            }

            else -> {
                val topic = current.todayLessonTopicId?.let { id ->
                    GrammarCurriculum.topicsByLevel[current.level].orEmpty().firstOrNull { it.id == id }
                }
                if (topic != null) {
                    Text(
                        text = if (current.todayLessonDone) "Today's lesson is done \u2014 nice!"
                        else "Today's lesson",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = { onOpenLesson(topic) }, modifier = Modifier.fillMaxWidth()) {
                        Text(topic.title)
                    }
                    if (current.todayLessonDone) {
                        OutlinedButton(
                            onClick = { onExtraPractice(topic) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Extra practice on this topic")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(onClick = onStartPlacement, modifier = Modifier.fillMaxWidth()) {
            Text("Retake the placement test")
        }
    }
}

@Composable
fun LessonScreen(
    topic: Topic,
    onExit: () -> Unit,
    onStartPractice: (Topic) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onExit) { Text("\u2190 Back") }
        Text(
            text = topic.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(text = topic.explanation, style = MaterialTheme.typography.bodyLarge)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Examples",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                topic.examples.forEach { example ->
                    Text(text = "\u2022 $example", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "\ud83d\udca1 Remember",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = topic.tip, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { onStartPractice(topic) }, modifier = Modifier.fillMaxWidth()) {
            Text("Practice this lesson")
        }
    }
}

@Composable
fun QuizScreen(
    title: String,
    questions: List<QuizQuestion>,
    subtitle: String,
    onDone: (correct: Int, total: Int) -> Unit,
    onExit: () -> Unit
) {
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var correctCount by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    if (finished) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Text(
                text = "\u2705 $correctCount / ${questions.size}",
                style = MaterialTheme.typography.displaySmall
            )
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Button(
                onClick = { onDone(correctCount, questions.size) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        return
    }

    val question = questions[index]
    val options = shuffledOptions(question)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onExit) { Text("Exit") }
        }
        LinearProgressIndicator(
            progress = { (index + 1).toFloat() / questions.size },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Question ${index + 1} of ${questions.size} \u00b7 $subtitle",
            style = MaterialTheme.typography.bodySmall
        )
        Text(text = question.question, style = MaterialTheme.typography.titleLarge)

        options.forEach { option ->
            val picked = selected != null &&
                (selected == option.originalIndex || option.isCorrect)
            val colors: CardColors = when {
                selected == null -> CardDefaults.cardColors()
                option.isCorrect -> CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
                picked -> CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
                else -> CardDefaults.cardColors()
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = colors,
                onClick = {
                    if (selected == null) {
                        selected = option.originalIndex
                        if (option.isCorrect) correctCount++
                    }
                }
            ) {
                Text(
                    text = option.text,
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        if (selected != null) {
            Text(
                text = question.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = {
                    if (index + 1 >= questions.size) {
                        finished = true
                    } else {
                        index++
                        selected = null
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (index + 1 >= questions.size) "See my result" else "Next question")
            }
        }
    }
}

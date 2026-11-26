package com.example.grammardaily.ui

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.grammardaily.data.Exercise
import com.example.grammardaily.data.ExerciseEngine
import com.example.grammardaily.data.GrammarCurriculum
import com.example.grammardaily.data.Level
import com.example.grammardaily.data.Placement
import com.example.grammardaily.data.ProgressStore
import com.example.grammardaily.data.ProgressState
import com.example.grammardaily.data.QuestionBank
import com.example.grammardaily.data.QuizQuestion
import com.example.grammardaily.data.Rules
import com.example.grammardaily.data.Topic

sealed interface AppScreen {
    data object Home : AppScreen
    data object Placement : AppScreen
    data class Lesson(val topic: Topic) : AppScreen
    data class TopicPractice(val topic: Topic, val daily: Boolean, val exercises: List<Exercise>) : AppScreen
    data class LevelUpTest(val questions: List<QuizQuestion>) : AppScreen
    data class Review(val topics: List<Topic>) : AppScreen
    data object VerbSettings : AppScreen
    data object VerbExercise : AppScreen
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
    onSkipTest: () -> Unit,
    onOpenLesson: (Topic) -> Unit,
    onStartLevelUp: (List<QuizQuestion>) -> Unit,
    onExtraPractice: (Topic) -> Unit,
    onStartReview: (List<Topic>) -> Unit,
    onOpenVerbSettings: () -> Unit
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
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Grammar Daily",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (!current.placementDone) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Learn English grammar, a few minutes a day",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Start learning right away \u2014 no test needed. Want to know your exact level first? There is a short 15-question check.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onSkipTest, modifier = Modifier.fillMaxWidth()) {
                        Text("Start learning \u2014 from B1")
                    }
                    OutlinedButton(onClick = onStartPlacement, modifier = Modifier.fillMaxWidth()) {
                        Text("Check my level first (15 questions)")
                    }
                }
            }
            return@Column
        }

        val levelTopics = GrammarCurriculum.topicsByLevel[current.level].orEmpty()
        val masteredCount = levelTopics.count { it.id in current.masteredTopicIds }
        val levelMastered = GrammarCurriculum.levelIsMastered(current.level, current.masteredTopicIds)
        val todayTopic = current.todayLessonTopicId?.let { id ->
            levelTopics.firstOrNull { it.id == id }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${current.level.code} \u00b7 ${current.level.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (current.streakDays > 0) "\uD83D\uDD25 ${current.streakDays} d"
                        else "day 0",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                LinearProgressIndicator(
                    progress = {
                        if (levelTopics.isEmpty()) 0f else masteredCount.toFloat() / levelTopics.size
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$masteredCount of ${levelTopics.size} lessons mastered",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                when {
                    current.allMasteredFinished -> {
                        Text(
                            text = "You walked the whole path \u2014 every lesson is mastered. \uD83C\uDF89",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedButton(
                            onClick = {
                                GrammarCurriculum.topicsByLevel[Level.A1]?.firstOrNull()?.let(onExtraPractice)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Keep practicing") }
                    }

                    levelMastered -> {
                        Button(
                            onClick = {
                                current.level.next?.let { next ->
                                    onStartLevelUp(QuestionBank.levelUpQuestions(next))
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Level ${current.level.code} done \u2192 take the level-up test") }
                    }

                    todayTopic != null -> {
                        Button(onClick = { onOpenLesson(todayTopic) }, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                if (current.todayLessonDone) "Today's lesson is done \u2014 open it again"
                                else "Today's lesson: ${todayTopic.title}"
                            )
                        }
                        if (current.todayLessonDone) {
                            OutlinedButton(
                                onClick = { onExtraPractice(todayTopic) },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Extra practice (nothing at stake)") }
                        }
                    }
                }
            }
        }

        if (current.dueReviewTopics.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "\uD83D\uDD01 Reviews due: ${current.dueReviewTopics.size}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { onStartReview(current.dueReviewTopics) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Do the reviews") }
                }
            }
        }

        val weakTopics = current.weakTopicIds.mapNotNull { ProgressStore.findTopicById(it) }
        if (weakTopics.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "\u26A0\uFE0F Weak spots \u2014 practice one:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    weakTopics.forEach { weak ->
                        TextButton(onClick = { onExtraPractice(weak) }) {
                            Text("\u2022 ${weak.title}")
                        }
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "\uD83D\uDCD8 Daily verb widget",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(onClick = onOpenVerbSettings, modifier = Modifier.fillMaxWidth()) {
                    Text("Choose verbs & schedule")
                }
            }
        }

        AiTeacherCard(refresh = refresh)

        TextButton(onClick = onStartPlacement, modifier = Modifier.fillMaxWidth()) {
            Text("Retake the level test")
        }
    }
}

@Composable
fun LessonScreen(
    topic: Topic,
    onExit: () -> Unit,
    onStartPractice: (Topic) -> Unit
) {
    val discovery = topic.discovery
    var discoveryAnswered by remember(topic.id) { mutableStateOf(false) }
    var discoveryCorrect by remember(topic.id) { mutableStateOf(false) }
    var picked by remember(topic.id) { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onExit) { Text("\u2190 Back") }
        Text(
            text = topic.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (discovery != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Step 1 \u2014 Notice",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    discovery.examples.forEach { example ->
                        Text(text = "\u2022 $example", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = discovery.question,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    discovery.options.forEachIndexed { index, option ->
                        OutlinedButton(
                            onClick = {
                                picked = index
                                discoveryAnswered = true
                                discoveryCorrect = index == discovery.correctIndex
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(option)
                        }
                    }
                    if (discoveryAnswered) {
                        Text(
                            text = if (discoveryCorrect) {
                                "\u2705 Exactly! " + discovery.explanation
                            } else {
                                "\u274C Not quite. " + discovery.explanation + " Try again."
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        val rule = remember(topic.id) { Rules.forTopic(topic.id) }
        val ruleUnlocked = discovery == null || discoveryCorrect
        if (ruleUnlocked) {
            if (discovery != null) {
                Text(
                    text = "Now the rule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (rule != null) {
                if (rule.timeline.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = rule.timeline,
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Pattern",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = rule.pattern,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (rule.short.isNotBlank()) {
                            Text(text = rule.short, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Look \u2014 the changing word is colored",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (rule.examples.isNotEmpty()) {
                            rule.examples.forEach { example ->
                                HighlightedText(text = "\u2022 $example", style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            topic.examples.forEach { example ->
                                HighlightedText(text = "\u2022 $example", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                if (rule.wrong.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "\u2718 ${rule.wrong}",
                                color = Color(0xFFB3261E),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "\u2714 ${rule.right}",
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = rule.why,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
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
        } else {
            Text(
                text = "Look at the sentences above and answer the noticing question \u2014 the rule unlocks once you spot the pattern yourself.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (ruleUnlocked) {
            val tryExercise = remember(topic.id) { ExerciseEngine.tryExercise(topic.id) }
            var tryDone by remember(topic.id) { mutableStateOf(tryExercise == null) }
            if (tryExercise != null && !tryDone) {
                Text(
                    text = "Try it — one easy question",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                ExerciseItem(
                    exercise = tryExercise,
                    showHint = true,
                    onAnswered = { _, _ -> },
                    onNext = { tryDone = true },
                    nextLabel = "Next \u2192 practice"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (tryDone) {
                Text(
                    text = "Time needed: about 4 minutes. After today, a one-question review waits for you tomorrow, in 3 days and in 7 days \u2014 pass them all and the lesson is truly yours.",
                    style = MaterialTheme.typography.bodySmall
                )
                Button(onClick = { onStartPractice(topic) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Practice this lesson (5 questions)")
                }
            }
        }
    }
}

@Composable
fun HighlightedText(text: String, style: TextStyle) {
    val parts = text.split("**")
    Text(
        text = buildAnnotatedString {
            parts.forEachIndexed { i, part ->
                if (i % 2 == 1) {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) { append(part) }
                } else {
                    append(part)
                }
            }
        },
        style = style
    )
}

private data class QuizUiState(
    val index: Int = 0,
    val selected: Int? = null,
    val correctCount: Int = 0,
    val finished: Boolean = false
)

@Composable
fun QuizScreen(
    title: String,
    questions: List<QuizQuestion>,
    subtitle: String,
    passFraction: Double = 0.6,
    showHints: Boolean = false,
    onDone: (correct: Int, total: Int, answers: List<Boolean>) -> Unit,
    onExit: () -> Unit
) {
    var state by remember { mutableStateOf(QuizUiState()) }
    var hintShown by remember(state.index) { mutableStateOf(false) }
    val answers = remember(questions) { mutableStateOf(listOf<Boolean>()) }

    if (state.finished) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Text(
                text = "\u2705 ${state.correctCount} / ${questions.size}",
                style = MaterialTheme.typography.displaySmall
            )
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            val passed = questions.isNotEmpty() &&
                state.correctCount.toDouble() / questions.size >= passFraction
            Text(
                text = if (passed) "Passed \u2014 spaced reviews will keep it alive in your memory."
                else "Not passed yet \u2014 no problem. The wrong answers showed the rule; come back to it anytime.",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = { onDone(state.correctCount, questions.size, answers.value) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        return
    }

    val question = questions[state.index]
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
            progress = { (state.index + 1).toFloat() / questions.size },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Question ${state.index + 1} of ${questions.size} \u00b7 $subtitle",
            style = MaterialTheme.typography.bodySmall
        )
        Text(text = question.question, style = MaterialTheme.typography.titleLarge)

        if (showHints && question.hint.isNotBlank() && state.selected == null) {
            if (!hintShown) {
                TextButton(onClick = { hintShown = true }) { Text("\ud83d\udca1 Show hint") }
            } else {
                Text(
                    text = "\ud83d\udca1 ${question.hint}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        options.forEach { option ->
            val picked = state.selected != null &&
                (state.selected == option.originalIndex || option.isCorrect)
            val colors: CardColors = when {
                state.selected == null -> CardDefaults.cardColors()
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
                    if (state.selected == null) {
                        state = state.copy(
                            selected = option.originalIndex,
                            correctCount = if (option.isCorrect) state.correctCount + 1 else state.correctCount
                        )
                        answers.value = answers.value + option.isCorrect
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

        if (state.selected != null) {
            Text(
                text = question.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = {
                    if (state.index + 1 >= questions.size) {
                        state = state.copy(finished = true)
                    } else {
                        state = state.copy(index = state.index + 1, selected = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.index + 1 >= questions.size) "See my result" else "Next question")
            }
        }
    }
}

@Composable
fun ReviewScreen(
    topics: List<Topic>,
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    var topicIndex by remember { mutableIntStateOf(0) }
    var attempt by remember { mutableIntStateOf(0) }
    var reTeach by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(topics.isEmpty()) }

    if (done) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Text("\ud83c\udf89 Reviews finished", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Topics you missed were re-taught and will come back sooner. Passed topics move closer to mastery.",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onFinished, modifier = Modifier.fillMaxWidth()) { Text("Done") }
            Spacer(modifier = Modifier.weight(1f))
        }
        return
    }

    val topic = topics[topicIndex]
    val exercise = remember(topic.id, attempt) {
        ExerciseEngine.reviewExercise(topic.id, attempt)
    }

    if (exercise == null) {
        val next = topicIndex + 1
        if (next >= topics.size) {
            done = true
        } else {
            topicIndex = next
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Review ${topicIndex + 1} of ${topics.size}: ${topic.title}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onFinished) { Text("Exit") }
        }

        if (reTeach) {
            val rule = Rules.forTopic(topic.id)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Quick re-teach", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (rule != null) {
                        Text(text = rule.pattern, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(text = rule.right, color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodyMedium)
                        if (rule.why.isNotBlank()) Text(text = rule.why, style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text(text = topic.explanation, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(text = topic.tip, style = MaterialTheme.typography.bodySmall)
                }
            }
            Button(
                onClick = {
                    attempt = attempt + 1
                    reTeach = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Got it \u2014 ask me a different one")
            }
        } else {
            ExerciseItem(
                exercise = exercise,
                showHint = true,
                onAnswered = { correct, _ ->
                    ProgressStore.recordAnswers(
                        context,
                        listOf(com.example.grammardaily.data.ExerciseResult(topic.id, exercise.kind, correct))
                    )
                    if (!correct) {
                        reTeach = true
                    } else {
                        ProgressStore.markReviewCorrect(context, topic.id)
                        val next = topicIndex + 1
                        if (next >= topics.size) {
                            done = true
                        } else {
                            topicIndex = next
                        }
                    }
                },
                onNext = null,
                nextLabel = ""
            )
        }
    }
}

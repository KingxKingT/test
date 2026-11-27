package com.example.grammardaily.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.grammardaily.data.Exercise
import com.example.grammardaily.data.ChunkBank
import com.example.grammardaily.data.ExerciseEngine
import com.example.grammardaily.data.ExerciseResult
import com.example.grammardaily.data.GrammarCurriculum
import com.example.grammardaily.data.Level
import com.example.grammardaily.data.Placement
import com.example.grammardaily.data.ProgressStore
import com.example.grammardaily.data.ProgressState
import com.example.grammardaily.data.QuestionBank
import com.example.grammardaily.data.QuizQuestion
import com.example.grammardaily.data.Rules
import com.example.grammardaily.data.Topic
import com.example.grammardaily.data.TranslateBank

sealed interface AppScreen {
    data object Home : AppScreen
    data object Placement : AppScreen
    data class Lesson(val topic: Topic) : AppScreen
    data class TopicPractice(val topic: Topic, val daily: Boolean, val exercises: List<Exercise>) : AppScreen
    data class LevelUpTest(val questions: List<QuizQuestion>) : AppScreen
    data class Review(val topics: List<Topic>) : AppScreen
    data object VerbSettings : AppScreen
    data object VerbExercise : AppScreen
    data object Essay : AppScreen
    data class QuickRecall(val exercises: List<Exercise>) : AppScreen
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
    onOpenVerbSettings: () -> Unit,
    onOpenEssay: () -> Unit,
    onQuickRecall: () -> Unit,
    textScale: Float,
    onTextScale: (Float) -> Unit
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
        val doneTopic = current.lessonDoneTopicId?.let { ProgressStore.findTopicById(it) }
        val weakTopics = current.weakTopicIds.mapNotNull { ProgressStore.findTopicById(it) }

        val headline = when {
            current.allMasteredFinished -> "You walked the whole path \uD83C\uDF89"
            todayTopic != null && !current.todayLessonDone -> "Today's lesson"
            levelMastered -> "Level ${current.level.code} is done!"
            current.dueReviewTopics.isNotEmpty() -> "Reviews are due"
            current.todayLessonDone -> "Today's lesson is done \u2705"
            else -> "Keep the streak alive"
        }
        val subline = when {
            current.allMasteredFinished -> "Every lesson is mastered. Keep reading real English every day."
            todayTopic != null && !current.todayLessonDone -> todayTopic.title
            levelMastered -> "All ${levelTopics.size} lessons mastered. Prove it and move up."
            current.dueReviewTopics.isNotEmpty() -> "${current.dueReviewTopics.size} past lessons came back \u2014 pass them and they stay yours."
            current.todayLessonDone -> "Same time tomorrow \u2014 the review is already scheduled."
            else -> "One lesson today is enough. Little and often wins."
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = headline,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (current.streakDays > 0) {
                            Text(
                                text = "\uD83D\uDD25 ${current.streakDays}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        if (current.streakFreezes > 0) {
                            Text(
                                text = "\uD83E\uDDCA",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                Text(
                    text = subline,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Button(
                    onClick = {
                        when {
                            current.allMasteredFinished ->
                                GrammarCurriculum.topicsByLevel[Level.A1]?.firstOrNull()?.let(onExtraPractice)
                            todayTopic != null && !current.todayLessonDone -> onOpenLesson(todayTopic)
                            levelMastered -> current.level.next?.let {
                                onStartLevelUp(QuestionBank.levelUpQuestions(it))
                            }
                            current.dueReviewTopics.isNotEmpty() -> onStartReview(current.dueReviewTopics)
                            current.todayLessonDone -> (doneTopic ?: todayTopic)?.let(onExtraPractice)
                            else -> levelTopics.firstOrNull {
                                it.id !in current.masteredTopicIds
                            }?.let(onExtraPractice)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        when {
                            current.allMasteredFinished -> "Keep practicing"
                            todayTopic != null && !current.todayLessonDone -> "Start today's lesson"
                            levelMastered -> "Take the level-up test"
                            current.dueReviewTopics.isNotEmpty() -> "Do the reviews"
                            current.todayLessonDone -> "Practice it once more"
                            else -> "Practice something from your level"
                        }
                    )
                }
                val learnedIds = current.activeBoxTopicIds + current.masteredTopicIds
                if (learnedIds.isNotEmpty()) {
                    TextButton(
                        onClick = onQuickRecall,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (current.quickRecallDone)
                                "\u2713 Quick recall done \u2014 memory locked for today"
                            else
                                "\u26A1 Quick recall \u2014 1 minute, keeps the streak",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = {
                        if (levelTopics.isEmpty()) 0f else masteredCount.toFloat() / levelTopics.size
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$masteredCount of ${levelTopics.size} lessons mastered \u00b7 Level ${current.level.code}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        var openSection by remember { mutableStateOf<String?>(null) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (current.dueReviewTopics.isNotEmpty()) {
                Pill(
                    text = "\uD83D\uDD01 Reviews \u00b7 ${current.dueReviewTopics.size}",
                    onClick = { onStartReview(current.dueReviewTopics) }
                )
            }
            if (weakTopics.isNotEmpty()) {
                Pill(
                    text = "\u26A0\uFE0F Weak \u00b7 ${weakTopics.size}",
                    active = openSection == "weak",
                    onClick = { openSection = if (openSection == "weak") null else "weak" }
                )
            }
            Pill(
                text = "\uD83D\uDCD6 Path",
                active = openSection == "path",
                onClick = { openSection = if (openSection == "path") null else "path" }
            )
            Pill(text = "\uD83D\uDCDD Essay", onClick = { onOpenEssay() })
            Pill(text = "\uD83D\uDCD8 Verbs", onClick = { onOpenVerbSettings() })
            Pill(
                text = "\uD83E\uDDE0 Laya",
                active = openSection == "ai",
                onClick = { openSection = if (openSection == "ai") null else "ai" }
            )
            Pill(
                text = "\u2699\uFE0F Settings",
                active = openSection == "settings",
                onClick = { openSection = if (openSection == "settings") null else "settings" }
            )
        }

        when (openSection) {
            "path" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Your path in ${current.level.code}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        val upcoming = levelTopics.filter {
                            it.id !in current.masteredTopicIds && it.id != todayTopic?.id
                        }
                        if (todayTopic != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "\u2192", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = todayTopic.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        upcoming.take(3).forEachIndexed { i, t ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "${i + 1}.", style = MaterialTheme.typography.bodyMedium)
                                Text(text = t.title, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        val more = upcoming.size - 3
                        if (more > 0) {
                            Text(
                                text = "+ $more more lessons in your path",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            "weak" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "\u26A0\uFE0F Weak spots \u2014 practice one:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        weakTopics.forEach { weak ->
                            TextButton(onClick = { onExtraPractice(weak) }) {
                                Text("\u2022 ${weak.title}")
                            }
                        }
                    }
                }
            }
            "ai" -> AiTeacherCard(refresh = refresh)
            "settings" -> {
                var showLangPicker by remember { mutableStateOf(false) }
                val translateLang = remember { mutableStateOf(ProgressStore.translateLang(context)) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Translate exercises",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { showLangPicker = true }) {
                        Text(
                            text = translateLang.value?.let { TranslateBank.languageName(it) } ?: "Off",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (showLangPicker) {
                    AlertDialog(
                        onDismissRequest = { showLangPicker = false },
                        confirmButton = {},
                        title = { Text("Translate from your language") },
                        text = {
                            Column {
                                TextButton(onClick = {
                                    ProgressStore.setTranslateLang(context, null)
                                    translateLang.value = null
                                    showLangPicker = false
                                }) { Text(if (translateLang.value == null) "Off \u2713" else "Off") }
                                TranslateBank.languages.forEach { (code, name) ->
                                    TextButton(onClick = {
                                        ProgressStore.setTranslateLang(context, code)
                                        translateLang.value = code
                                        showLangPicker = false
                                    }) {
                                        Text(if (translateLang.value == code) "$name \u2713" else name)
                                    }
                                }
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Text size",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                        listOf(1f to "A", 1.25f to "A+", 1.5f to "A++").forEach { (value, label) ->
                            TextButton(onClick = { onTextScale(value) }) {
                                Text(
                                    text = if (textScale == value) label + " \u2713" else label,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                TextButton(onClick = onStartPlacement, modifier = Modifier.fillMaxWidth()) {
                    Text("Retake the level test")
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun Pill(text: String, active: Boolean = false, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(
            containerColor = if (active) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LessonScreen(
    topic: Topic,
    onExit: () -> Unit,
    onStartPractice: (Topic) -> Unit
) {
    val discovery = topic.discovery
    val quickTry = remember(topic.id) { ExerciseEngine.typePool(topic.id).firstOrNull() }
    var discoveryAnswered by remember(topic.id) { mutableStateOf(false) }
    var discoveryCorrect by remember(topic.id) { mutableStateOf(false) }
    var picked by remember(topic.id) { mutableStateOf(-1) }
    val context = LocalContext.current

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
                TextButton(onClick = {
                    Audio.init(context)
                    Audio.speak(rule.pattern + ". " + topic.examples.joinToString(". "))
                }) { Text("\uD83D\uDD0A Hear this pattern") }
                quickTry?.let { ex ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Try it now \u2014 no pressure",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            ExerciseItem(
                                exercise = ex,
                                showHint = true,
                                onAnswered = { correct, _ ->
                                    ProgressStore.recordAnswers(
                                        context,
                                        listOf(ExerciseResult(ex.topicId, ex.kind, correct))
                                    )
                                },
                                onNext = null,
                                nextLabel = ""
                            )
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
                ChunkBank.byTopic[topic.id]?.takeIf { it.isNotEmpty() }?.let { chunks ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "\uD83D\uDCA1 Say these as one block",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            chunks.forEach { chunk ->
                                Text(text = chunk, style = MaterialTheme.typography.bodyMedium)
                            }
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
                            textDecoration = TextDecoration.Underline,
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

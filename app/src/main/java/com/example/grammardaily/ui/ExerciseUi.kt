package com.example.grammardaily.ui

@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.grammardaily.data.ExKind
import com.example.grammardaily.data.Exercise
import com.example.grammardaily.data.ExerciseEngine
import com.example.grammardaily.data.ExerciseResult
import com.example.grammardaily.data.Placement
import com.example.grammardaily.data.PlacementResult
import com.example.grammardaily.data.Topic

object Audio {
    @Volatile
    private var tts: TextToSpeech? = null

    val ready = mutableStateOf(false)

    fun init(context: Context) {
        if (tts == null) {
            synchronized(this) {
                if (tts == null) {
                    tts = TextToSpeech(context.applicationContext) { status ->
                        ready.value = status == TextToSpeech.SUCCESS
                    }
                }
            }
        }
    }

    fun speak(text: String) {
        if (ready.value) {
            tts?.setSpeechRate(0.85f)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "grammar-daily")
        }
    }
}

private data class ExOption(val originalIndex: Int, val text: String, val isCorrect: Boolean)

@Composable
private fun feedbackColors(selected: Int?, option: ExOption, checked: Boolean): CardColors = when {
    !checked -> CardDefaults.cardColors()
    option.isCorrect -> CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )
    selected == option.originalIndex -> CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.errorContainer
    )
    else -> CardDefaults.cardColors()
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    showHint: Boolean,
    onAnswered: (correct: Boolean) -> Unit,
    onNext: (() -> Unit)?,
    nextLabel: String
) {
    val context = LocalContext.current
    var typed by remember(exercise) { mutableStateOf("") }
    var checked by remember(exercise) { mutableStateOf(false) }
    var wasCorrect by remember(exercise) { mutableStateOf(false) }
    var selected by remember(exercise) { mutableIntStateOf(-1) }
    var placedIdx by remember(exercise) { mutableStateOf(listOf<Int>()) }
    var showAudioText by remember(exercise) { mutableStateOf(false) }
    var hintOpen by remember(exercise) { mutableStateOf(false) }

    val target = exercise.accepted.firstOrNull() ?: exercise.answer

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (exercise.kind == ExKind.MCQ) {
            Text(text = exercise.prompt, style = MaterialTheme.typography.titleLarge)
        } else if (exercise.kind == ExKind.REORDER) {
            Text(text = exercise.prompt, style = MaterialTheme.typography.titleLarge)
        } else if (exercise.kind == ExKind.TYPE) {
            Text(text = exercise.prompt, style = MaterialTheme.typography.titleLarge)
            Text(
                text = exercise.sentence,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        } else if (exercise.kind == ExKind.FIX) {
            Text(
                text = "\u2718 " + exercise.sentence,
                color = Color(0xFFB3261E),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(text = exercise.prompt, style = MaterialTheme.typography.titleLarge)
        } else {
            Text(text = exercise.prompt, style = MaterialTheme.typography.titleLarge)
            LaunchedEffect(exercise) { Audio.init(context) }
            Button(
                onClick = { Audio.speak(exercise.sentence) },
                enabled = Audio.ready.value
            ) {
                Text(if (Audio.ready.value) "\U0001F50A Play the sentence" else "\U0001F50A Audio loading\u2026")
            }
            if (showAudioText) {
                Text(
                    text = exercise.sentence,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                TextButton(onClick = { showAudioText = true }) {
                    Text("No sound? Show the sentence and type it anyway")
                }
            }
        }

        when (exercise.kind) {
            ExKind.MCQ -> {
                val options = exercise.options.mapIndexed { i, t ->
                    ExOption(i, t, i == exercise.correctIndex)
                }
                val shown = remember(exercise) { options.shuffled() }
                shown.forEach { option ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = feedbackColors(selected, option, checked),
                        onClick = {
                            if (!checked) {
                                selected = option.originalIndex
                                checked = true
                                wasCorrect = option.isCorrect
                                onAnswered(option.isCorrect)
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
            }

            ExKind.REORDER -> {
                val remaining = exercise.words.mapIndexed { i, w -> i to w }
                    .filter { (i, _) -> i !in placedIdx }
                Text(
                    text = exercise.words.indices.map { i ->
                        if (i < placedIdx.size) exercise.words[placedIdx[i]] else "\u2026"
                    }.joinToString(" "),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                if (!checked) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        remaining.forEach { (i, w) ->
                            OutlinedButton(onClick = { placedIdx = placedIdx + i }) { Text(w) }
                        }
                    }
                    if (placedIdx.isNotEmpty()) {
                        TextButton(onClick = { placedIdx = placedIdx.dropLast(1) }) {
                            Text("\u2190 Undo last word")
                        }
                    }
                    Button(
                        onClick = {
                            val built = placedIdx.joinToString(" ") { exercise.words[it] }
                            checked = true
                            wasCorrect = ExerciseEngine.closeEnough(built, exercise.answer)
                            onAnswered(wasCorrect)
                        },
                        enabled = placedIdx.size == exercise.words.size,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Check") }
                }
            }

            ExKind.TYPE, ExKind.FIX, ExKind.LISTEN -> {
                if (!checked) {
                    OutlinedTextField(
                        value = typed,
                        onValueChange = { typed = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Type here") },
                        singleLine = exercise.kind == ExKind.TYPE
                    )
                    Button(
                        onClick = {
                            checked = true
                            wasCorrect = ExerciseEngine.closeEnough(typed, target)
                            onAnswered(wasCorrect)
                        },
                        enabled = typed.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Check") }
                }
            }
        }

        if (showHint && !checked && exercise.hint.isNotBlank()) {
            if (!hintOpen) {
                TextButton(onClick = { hintOpen = true }) { Text("\U0001F4A1 Show hint") }
            } else {
                Text(
                    text = "\U0001F4A1 ${exercise.hint}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        if (checked) {
            val shownCorrect = when (exercise.kind) {
                ExKind.MCQ -> exercise.options[exercise.correctIndex]
                ExKind.REORDER -> exercise.answer
                else -> target
            }
            if (wasCorrect) {
                Text(
                    text = "\u2705 Correct!",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2E7D32)
                )
            } else {
                Text(
                    text = "\u274C The right answer:",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFB3261E)
                )
                Text(text = shownCorrect, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            if (exercise.explanation.isNotBlank()) {
                Text(
                    text = exercise.explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onNext != null) {
                Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text(nextLabel) }
            }
        }
    }
}

@Composable
fun ExerciseRunner(
    title: String,
    subtitle: String,
    exercises: List<Exercise>,
    passFraction: Double,
    showHints: Boolean,
    passMessage: String,
    failMessage: String,
    onDone: (results: List<ExerciseResult>, correct: Int, total: Int) -> Unit,
    onExit: (() -> Unit)?
) {
    var index by remember(exercises) { mutableIntStateOf(0) }
    val results = remember(exercises) { mutableStateOf(listOf<ExerciseResult>()) }

    if (exercises.isEmpty()) {
        Text(text = "Nothing to practice yet.", modifier = Modifier.padding(20.dp))
        return
    }

    if (index >= exercises.size) {
        val correct = results.value.count { it.correct }
        val passed = correct.toDouble() / exercises.size >= passFraction
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Text(
                text = if (correct == exercises.size) "\U0001F389" else "\u2705",
                style = MaterialTheme.typography.displaySmall
            )
            Text(text = "$correct / ${exercises.size}", style = MaterialTheme.typography.displaySmall)
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = if (passed) passMessage else failMessage,
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = { onDone(results.value, correct, exercises.size) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Continue") }
            Spacer(modifier = Modifier.weight(1f))
        }
        return
    }

    val exercise = exercises[index]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (onExit != null) {
                TextButton(onClick = onExit) { Text("Exit") }
            }
        }
        LinearProgressIndicator(
            progress = { (index + 1).toFloat() / exercises.size },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Question ${index + 1} of ${exercises.size} \u00b7 $subtitle",
            style = MaterialTheme.typography.bodySmall
        )
        ExerciseItem(
            exercise = exercise,
            showHint = showHints,
            onAnswered = { correct ->
                results.value = results.value + ExerciseResult(exercise.topicId, exercise.kind, correct)
            },
            onNext = { index = index + 1 },
            nextLabel = if (index + 1 >= exercises.size) "See my result" else "Next question"
        )
    }
}

@Composable
fun PlacementScreen(
    onFinished: (PlacementResult) -> Unit,
    onExit: () -> Unit
) {
    val exercises = remember { Placement.mixedTest() }
    var result by remember { mutableStateOf<PlacementResult?>(null) }

    if (result != null) {
        val res = result!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onExit) { Text("\u2190 Back") }
            Text(
                text = "Your level: ${res.level.code} \u00b7 ${res.level.title}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Score per grammar topic:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            res.scores.forEach { (topic, c, t) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${topic.title} (${res.levelCodeOf(topic)})", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "$c/$t",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (c == t) Color(0xFF2E7D32) else if (c == 0) Color(0xFFB3261E) else Color.Unspecified
                    )
                }
            }
            if (res.studyNext.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Study this next:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                res.studyNext.forEach { topic ->
                    Text(text = "\u2022 ${topic.title}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { onFinished(res) }, modifier = Modifier.fillMaxWidth()) {
                Text("Start learning at ${res.level.code}")
            }
        }
        return
    }

    ExerciseRunner(
        title = "Full grammar test",
        subtitle = "type, reorder, fix and pick \u2014 answer honestly",
        exercises = exercises,
        passFraction = 1.0,
        showHints = false,
        passMessage = "Strong level reached \u2014 details below.",
        failMessage = "Details below.",
        onDone = { results, _, _ ->
            result = Placement.evaluate(exercises, results)
        },
        onExit = onExit
    )
}

private fun PlacementResult.levelCodeOf(topic: Topic): String = topic.id.substringBefore(":")

@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.grammardaily.data.ExKind
import com.example.grammardaily.data.Exercise
import com.example.grammardaily.data.ExerciseResult
import com.example.grammardaily.data.Level
import com.example.grammardaily.data.ProgressStore
import com.example.grammardaily.data.VerbBank
import com.example.grammardaily.data.VerbStore
import com.example.grammardaily.widget.VerbWidget
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.glance.appwidget.updateAll
import com.example.grammardaily.data.AiJudge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun VerbExerciseScreen(
    onDone: () -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var descriptor by remember { mutableStateOf(VerbStore.currentCard(context) ?: "go|0|0") }
    var solved by remember { mutableStateOf(false) }

    val parts = descriptor.split("|")
    val event = remember(descriptor) {
        runCatching {
            VerbBank.event(parts[0], parts[1].toInt(), parts[2].toInt())
        }.getOrElse { VerbBank.event("go", 0, 0) }
    }

    val exercise = remember(descriptor) {
        if (event.form == 2) {
            val words = event.main.replace("?", "").split(" ").filter { it.isNotBlank() }
            var scrambled = words.shuffled()
            var guard = 0
            while (scrambled.joinToString(" ") == words.joinToString(" ") && guard < 10) {
                scrambled = words.shuffled()
                guard++
            }
            Exercise(
                topicId = "verb",
                kind = ExKind.REORDER,
                prompt = "Rebuild the sentence from the widget",
                words = scrambled,
                answer = words.joinToString(" "),
                explanation = event.useLine,
                hint = ""
            )
        } else {
            val sentence = runCatching {
                Regex("\\b" + Regex.escape(event.targetForm) + "\\b", RegexOption.IGNORE_CASE)
                    .replaceFirst(event.main, "___")
            }.getOrElse { event.main }
            Exercise(
                topicId = "verb",
                kind = ExKind.TYPE,
                prompt = "Fill the blank - the ${event.tenseName} form of \u201c${event.verb}\u201d",
                sentence = sentence,
                accepted = listOf(event.targetForm),
                explanation = event.useLine,
                hint = "Forms: ${event.formsLine}"
            )
        }
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Daily verb \u00b7 ${event.verb}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onExit) { Text("Exit") }
        }
        Text(
            text = event.tenseName,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.tertiary
        )

        if (!solved) {
            ExerciseItem(
                exercise = exercise,
                showHint = true,
                onAnswered = { _, _ -> },
                onNext = {
                    solved = true
                    ProgressStore.recordAnswers(
                        context,
                        listOf(ExerciseResult("verb", exercise.kind, true))
                    )
                },
                nextLabel = "Next card"
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(event.main, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(event.useLine, style = MaterialTheme.typography.bodyMedium)
                    Text(event.alt, style = MaterialTheme.typography.bodySmall)
                }
            }
            Button(
                onClick = {
                    scope.launch(Dispatchers.Main) {
                        VerbStore.rotate(context)
                        VerbWidget().updateAll(context)
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Next card") }
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Back home")
            }
        }
    }
}

@Composable
fun VerbSettingsScreen(
    onExit: () -> Unit
) {
    var refresh by remember { mutableStateOf(0) }
    var newVerb by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Daily verb widget",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onExit) { Text("Done") }
        }

        val active = remember(refresh) { VerbStore.activeVerbs(context) }
        val mastered = remember(refresh) { VerbStore.masteredVerbs(context) }
        val suggestions = remember(refresh) {
            (VerbBank.suggestions(Level.A1) + VerbBank.suggestions(Level.B1)).distinct()
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Your active verbs (max 3)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                active.forEach { verb ->
                    val fraction = remember(refresh, verb) {
                        VerbStore.knownFraction(VerbStore.knownCards(context), verb)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = VerbBank.forms(verb).display() + "  (" + (fraction * 100).toInt() + "% known)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = {
                            VerbStore.setActiveVerbs(context, active - verb)
                            refresh++
                        }) { Text("Remove") }
                    }
                }
                if (active.size < 3) {
                    OutlinedTextField(
                        value = newVerb,
                        onValueChange = { newVerb = it },
                        label = { Text("Add a verb (e.g. play)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Button(onClick = {
                        val v = newVerb.trim().lowercase(Locale.ROOT)
                        if (v.length < 2 || !v.all { it.isLetter() }) {
                            error = "Type a simple English verb, letters only."
                        } else if (v in active) {
                            error = "Already active."
                        } else {
                            error = null
                            VerbStore.setActiveVerbs(context, active + v)
                            newVerb = ""
                            refresh++
                        }
                    }) { Text("Add") }
                    error?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Suggested verbs", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestions.filter { it !in active }.take(8).forEach { verb ->
                        OutlinedButton(
                            onClick = {
                                if (active.size < 3) {
                                    VerbStore.setActiveVerbs(context, active + verb)
                                    refresh++
                                }
                            },
                            enabled = active.size < 3
                        ) { Text(verb) }
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Times per day", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 2, 3, 6).forEach { n ->
                        OutlinedButton(onClick = { VerbStore.setTimesPerDay(context, n); refresh++ }) {
                            Text(if (VerbStore.timesPerDay(context) == n) "$n \u2713" else "$n")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Quiet hours (no card changes)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0 to "Off", 1 to "22-07", 2 to "23-08", 3 to "00-06").forEach { (mode, label) ->
                        OutlinedButton(
                            onClick = { VerbStore.setQuietHours(context, mode); refresh++ }
                        ) {
                            Text(if (VerbStore.quietHours(context) == mode) "$label \u2713" else label)
                        }
                    }
                }
            }
        }

        if (mastered.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "\uD83C\uDFC5 Mastered verbs",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    mastered.forEach { verb ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(VerbBank.forms(verb).display(), style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = {
                                val known = VerbStore.knownCards(context)
                                    .filterNot { it.startsWith("$verb|") }
                                context
                                    .getSharedPreferences("verb_widget", android.content.Context.MODE_PRIVATE)
                                    .edit()
                                    .putStringSet("known_cards", known.toSet())
                                    .putStringSet("mastered_verbs", (mastered - verb).toSet())
                                    .apply()
                                refresh++
                            }) { Text("Reset") }
                        }
                    }
                }
            }
        }

        AiTeacherCard(refresh = refresh)

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "The widget shows your verbs in a new tense and form several times a day, at random moments during your waking hours. Tap the card for a quick fill-the-blank or rebuild exercise.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun AiTeacherCard(refresh: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var ready by remember(refresh) { mutableStateOf(AiJudge.isModelReady(context)) }
    var status by remember(refresh) { mutableStateOf("") }
    var importing by remember { mutableStateOf(false) }
    var downProgress by remember { mutableStateOf<Pair<Long, Long>?>(null) }
    var downName by remember { mutableStateOf("") }
    var expanded by remember(refresh) { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            importing = true
            scope.launch(Dispatchers.IO) {
                val ok = AiJudge.importModel(context, uri)
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    importing = false
                    ready = ok
                    status = if (ok) {
                        val mb = AiJudge.modelFile(context).length() / (1024 * 1024)
                        "Model installed ($mb MB) \u2014 the AI judge is now active."
                    } else {
                        "Import failed \u2014 the file could not be read or is too small."
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "\uD83E\uDDE0 AI judge (optional)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            importing -> "Importing model\u2026"
                            downProgress != null -> {
                                val (d, t) = downProgress!!
                                "Downloading \u2014 ${d / (1024 * 1024)} of ${t / (1024 * 1024)} MB"
                            }
                            ready -> "Installed \u2014 judging your written answers, offline."
                            else -> "A small open-source AI can judge your written answers."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Hide" else "Open")
                }
            }
            if (expanded) {
                Text(
                    text = "Written answers are judged by the built-in checker. Download a small open-source AI (Gemma 3 1B) and it will judge them instead \u2014 accepting different wordings a simple checker would call wrong. It never talks; it only judges, fully offline.",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (status.isNotBlank()) {
                    Text(text = status, color = MaterialTheme.colorScheme.tertiary)
                }
                when {
                    downProgress != null -> {
                        val (d, t) = downProgress!!
                        LinearProgressIndicator(
                            progress = { (d.toDouble() / t.coerceAtLeast(1)).toFloat() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Downloading ${downName} \u2014 keep the app open",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    !ready -> {
                        Button(
                            onClick = {
                                downName = "AI model"
                                status = ""
                                scope.launch(Dispatchers.IO) {
                                    var ok = false
                                    for (model in AiJudge.downloadableModels) {
                                        kotlinx.coroutines.withContext(Dispatchers.Main) { downName = model.name }
                                        ok = AiJudge.downloadBlocking(context, model.url) { d, t ->
                                            downProgress = d to t
                                        }
                                        if (ok) break
                                    }
                                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                                        downProgress = null
                                        ready = ok
                                        status = if (ok)
                                            "Model downloaded \u2014 the AI judge is now active."
                                        else
                                            "Download failed \u2014 check your internet, or use Import below."
                                    }
                                }
                            },
                            enabled = !importing,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("\u2b07 Download the AI (529 MB)") }
                        Button(
                            onClick = { picker.launch("*/*") },
                            enabled = !importing,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Or import a model file from your phone") }
                        Text(
                            text = "The model is 529 MB and downloads once; afterwards everything runs offline. You can delete it anytime to free space.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> {
                        Button(
                            onClick = { picker.launch("*/*") },
                            enabled = !importing,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Replace the model \u2014 pick a file") }
                        OutlinedButton(
                            onClick = {
                                AiJudge.deleteModel(context)
                                ready = false
                                status = "Model deleted \u2014 the built-in checker judges again."
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Delete the model (free up space)") }
                    }
                }
            }
        }
    }
}

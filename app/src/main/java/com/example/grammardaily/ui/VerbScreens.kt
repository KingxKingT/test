@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.example.grammardaily.ui

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.grammardaily.data.CloudAi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
    var lastCorrect by remember { mutableStateOf(false) }

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
                onAnswered = { correct, _ -> lastCorrect = correct },
                onAppealed = { lastCorrect = true },
                onNext = {
                    solved = true
                    ProgressStore.recordAnswers(
                        context,
                        listOf(ExerciseResult("verb", exercise.kind, lastCorrect))
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
    var disabled by remember(refresh) { mutableStateOf(AiJudge.isDisabled(context)) }
    var status by remember(refresh) { mutableStateOf("") }
    var importing by remember { mutableStateOf(false) }
    var testLoading by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var cloudProviderIdx by remember { mutableStateOf(CloudAi.providerIndex(context)) }
    var cloudKey by remember { mutableStateOf(CloudAi.apiKey(context)) }
    var cloudEnabled by remember { mutableStateOf(CloudAi.isEnabled(context)) }
    var cloudStatus by remember { mutableStateOf("") }
    var cloudTesting by remember { mutableStateOf(false) }
    var customBase by remember { mutableStateOf(CloudAi.customBaseUrl(context)) }
    var customModel by remember { mutableStateOf(CloudAi.customModelName(context)) }
    val customIdx = CloudAi.providers.size
    var expanded by remember(refresh) { mutableStateOf(false) }
    val dl by AiJudge.downloadState.collectAsState()
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
                        "Model installed ($mb MB) \u2014 Laya and the AI are now active."
                    } else {
                        "Import failed \u2014 the file could not be read or is too small."
                    }
                }
            }
        }
    }

    LaunchedEffect(dl.done, dl.failed) {
        if (dl.done) {
            ready = true
            status = "Model downloaded \u2014 Laya and the AI are now active."
        } else if (dl.failed && !dl.running) {
            status = "Download stopped \u2014 it will continue from where it stopped when you press Download again."
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
                        text = "\uD83E\uDDE0 Laya & the AI (optional)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            importing -> "Importing model\u2026"
                            dl.running -> {
                                val mb = dl.downloaded / (1024 * 1024)
                                val totalMb = dl.total / (1024 * 1024)
                                if (totalMb > 0) "Downloading \u2014 $mb of $totalMb MB (${mb * 100 / totalMb.coerceAtLeast(1)}%)"
                                else "Downloading \u2014 $mb MB so far"
                            }
                            ready && !disabled -> "Installed \u2014 Laya judges answers, the AI handles essays, offline." + if (cloudEnabled) " Fast cloud AI: on." else ""
                            ready && disabled -> "Laya switched itself off after slow answers. Your model is still installed \u2014 no download needed."
                            else -> "One small download adds Laya (silent judge) and the AI (essays + level reports)."
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
                    text = "Written answers are checked by the built-in checker. Download the AI (Gemma 3 1B) and Laya judges your written answers \u2014 accepting different wordings a simple checker would call wrong \u2014 while the AI grades essays and level reports. Both run fully offline; Laya never talks.",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (status.isNotBlank()) {
                    Text(text = status, color = MaterialTheme.colorScheme.tertiary)
                }
                if (disabled && ready) {
                    Text(
                        text = "After 3 slow answers Laya switches itself off to keep the app fast. Your model is still on this phone \u2014 turning it back on needs no download.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                AiJudge.reEnable(context)
                                kotlinx.coroutines.withContext(Dispatchers.Main) {
                                    disabled = false
                                    ready = true
                                    status = "Laya is back on."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Turn the AI back on") }
                }
                when {
                    dl.running -> {
                        LinearProgressIndicator(
                            progress = { if (dl.total > 0) (dl.downloaded.toDouble() / dl.total).toFloat() else 0f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Downloading ${dl.name} \u2014 you can close this section or the app; the download keeps going and continues where it stopped.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    !ready -> {
                        val ramGb = remember { AiJudge.totalRamGb(context) }
                        val canBig = ramGb >= 6
                        val big = AiJudge.downloadableModels.last()
                        val small = AiJudge.downloadableModels.first()
                        Text(
                            text = if (canBig)
                                "Your phone has about $ramGb GB of memory \u2014 it can run the big, smart AI. Best for your phone: " + big.name + "."
                            else
                                "Your phone has about $ramGb GB of memory \u2014 the small, fast AI is the right fit. Best for your phone: " + small.name + ".",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = {
                                status = ""
                                AiJudge.startDownload(context, if (canBig) big else small)
                            },
                            enabled = !importing,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (canBig) "\u2b07 Download the best for your phone (2.4 GB)"
                                else "\u2b07 Download the AI (529 MB)"
                            )
                        }
                        if (canBig) {
                            OutlinedButton(
                                onClick = {
                                    status = ""
                                    AiJudge.startDownload(context, small)
                                },
                                enabled = !importing,
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("\u2b07 Or the small, fast AI (529 MB)") }
                        }
                        Button(
                            onClick = { picker.launch("*/*") },
                            enabled = !importing,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Or import a model file from your phone") }
                        Text(
                            text = "The model downloads once; afterwards everything runs offline. A stopped download continues from where it stopped \u2014 it never restarts from zero. You can delete it anytime to free space.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> {
                        Text(
                            text = "Stored safely inside the app's private storage \u2014 updates of the app do not delete it. Size: ${AiJudge.modelFile(context).length() / (1024 * 1024)} MB.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = {
                                testLoading = true
                                testResult = null
                                scope.launch(Dispatchers.IO) {
                                    val reply = AiJudge.askAiBlocking(context, "Reply with the single word OK.")
                                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                                        testLoading = false
                                        testResult = reply?.trim()
                                            ?: "No answer \u2014 the model file may be damaged. Delete it below and download again."
                                    }
                                }
                            },
                            enabled = !testLoading && !importing,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (testLoading) "\uD83E\uDDE0 Waking the AI\u2026" else "\uD83E\uDDE0 Test the AI \u2014 is it really working?") }
                        if (testResult != null) {
                            Text(
                                text = if (testResult == "OK" || testResult!!.startsWith("OK"))
                                    "\u2705 The AI answered \u2014 Laya and the AI are live on this phone."
                                else
                                    testResult!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (testResult == "OK" || testResult!!.startsWith("OK")) MaterialTheme.colorScheme.tertiary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                        Button(
                            onClick = { picker.launch("*/*") },
                            enabled = !importing,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Replace the model \u2014 pick a file") }
                        OutlinedButton(
                            onClick = {
                                AiJudge.deleteModel(context)
                                disabled = false
                                ready = false
                                testResult = null
                                status = "Model deleted \u2014 the built-in checker judges again."
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Delete the model (free up space)") }
                    }
                }
                Text(
                    text = "\u26A1 Fast cloud AI \u2014 makes everything much faster",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                val provider = CloudAi.providers[cloudProviderIdx]
                Text(
                    text = "The AI on this phone is small, so it can be slow. With a free API key, answers come from a fast cloud AI instead \u2014 about a second instead of half a minute. " + provider.note + ". Privacy: while this is on, your written answers and essays are sent to that company's servers to be checked. Turn it off and everything stays on your phone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CloudAi.providers.forEachIndexed { i, p ->
                        FilterChip(
                            selected = cloudProviderIdx == i,
                            onClick = {
                                CloudAi.setProvider(context, i)
                                cloudProviderIdx = i
                            },
                            label = { Text(p.label, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                    FilterChip(
                        selected = cloudProviderIdx == customIdx,
                        onClick = {
                            CloudAi.setProvider(context, customIdx)
                            cloudProviderIdx = customIdx
                        },
                        label = { Text("Custom", style = MaterialTheme.typography.bodySmall) }
                    )
                }
                if (cloudProviderIdx == customIdx) {
                    OutlinedTextField(
                        value = customBase,
                        onValueChange = { customBase = it },
                        label = { Text("Base URL (e.g. https://cheapllm.shop/v1)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customModel,
                        onValueChange = { customModel = it },
                        label = { Text("Model name (e.g. deepseek-v4.1-flash-1017)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(
                    value = cloudKey,
                    onValueChange = { cloudKey = it },
                    label = { Text("Paste your free API key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            if (cloudProviderIdx == customIdx) {
                                CloudAi.setCustomBaseUrl(context, customBase)
                                CloudAi.setCustomModelName(context, customModel)
                            }
                            CloudAi.setApiKey(context, cloudKey)
                            CloudAi.setEnabled(context, true)
                            cloudEnabled = true
                            cloudStatus = "Saved \u2014 cloud AI is ON. Answers now come from the fast cloud AI whenever the internet is up; offline it falls back to the AI on the phone."
                        },
                        enabled = cloudKey.isNotBlank() &&
                            (cloudProviderIdx != customIdx || (customBase.isNotBlank() && customModel.isNotBlank()))
                    ) { Text("Save and turn on") }
                    if (cloudEnabled) {
                        OutlinedButton(
                            onClick = {
                                CloudAi.setEnabled(context, false)
                                cloudEnabled = false
                                cloudStatus = "Cloud AI is off \u2014 the AI on the phone handles everything again."
                            }
                        ) { Text("Turn off") }
                        OutlinedButton(
                            onClick = {
                                if (cloudProviderIdx == customIdx) {
                                    CloudAi.setCustomBaseUrl(context, customBase)
                                    CloudAi.setCustomModelName(context, customModel)
                                }
                                CloudAi.setApiKey(context, cloudKey)
                                cloudTesting = true
                                scope.launch(Dispatchers.IO) {
                                    val r = CloudAi.testBlocking(context)
                                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                                        cloudTesting = false
                                        cloudStatus = r
                                    }
                                }
                            },
                            enabled = cloudKey.isNotBlank() && !cloudTesting &&
                                (cloudProviderIdx != customIdx || (customBase.isNotBlank() && customModel.isNotBlank()))
                        ) { Text(if (cloudTesting) "Testing\u2026" else "Test") }
                    }
                }
                if (cloudStatus.isNotBlank()) {
                    Text(text = cloudStatus, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    text = "Build 19 \u2014 cloud AI options: Kilo \u00b7 cheapllm \u00b7 Nvidia \u00b7 Groq \u00b7 OpenRouter \u00b7 Custom. If you cannot see this line, you are running an older app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

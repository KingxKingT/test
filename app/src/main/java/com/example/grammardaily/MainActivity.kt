package com.example.grammardaily

import android.Manifest
import android.os.Build
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.grammardaily.data.AiJudge
import com.example.grammardaily.data.Placement
import com.example.grammardaily.data.ProgressStore
import com.example.grammardaily.data.TextScale
import com.example.grammardaily.data.ExerciseEngine
import com.example.grammardaily.data.QuestionBank
import com.example.grammardaily.notify.ReminderWorker
import com.example.grammardaily.notify.VerbRotationWorker
import com.example.grammardaily.ui.AppScreen
import com.example.grammardaily.ui.HomeScreen
import com.example.grammardaily.ui.ExerciseRunner
import com.example.grammardaily.ui.LessonScreen
import com.example.grammardaily.ui.PlacementScreen
import com.example.grammardaily.ui.QuizScreen
import com.example.grammardaily.ui.ReviewScreen
import com.example.grammardaily.ui.VerbExerciseScreen
import com.example.grammardaily.ui.VerbSettingsScreen
import com.example.grammardaily.ui.EssayScreen
import com.example.grammardaily.widget.GrammarWidget
import androidx.glance.appwidget.updateAll

class MainActivity : ComponentActivity() {

    private val verbCardRequest = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verbCardRequest.value = intent?.getBooleanExtra("verb_card", false) == true
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(verbCardRequest)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        verbCardRequest.value = intent.getBooleanExtra("verb_card", false)
    }
}

@Composable
fun AppRoot(verbCardRequest: MutableState<Boolean>) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var textScale by remember { mutableFloatStateOf(TextScale.get(context)) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        ReminderWorker.schedule(context.applicationContext)
        VerbRotationWorker.schedule(context.applicationContext)
        AiJudge.warmUp(context.applicationContext)
    }

    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
    var homeRefresh by remember { mutableIntStateOf(0) }

    LaunchedEffect(screen) {
        GrammarWidget().updateAll(context.applicationContext)
    }

    fun goHome() {
        screen = AppScreen.Home
        homeRefresh++
    }

    LaunchedEffect(verbCardRequest.value) {
        if (verbCardRequest.value) {
            screen = AppScreen.VerbExercise
            verbCardRequest.value = false
        }
    }

    Scaffold { padding ->
        CompositionLocalProvider(
            LocalDensity provides Density(density.density, density.fontScale * textScale)
        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val current = screen) {
                is AppScreen.Home -> HomeScreen(
                    refresh = homeRefresh,
                    onStartPlacement = { screen = AppScreen.Placement },
                    onSkipTest = {
                        ProgressStore.markPlacement(com.example.grammardaily.data.Level.B1, context)
                        goHome()
                    },
                    onOpenLesson = { topic -> screen = AppScreen.Lesson(topic) },
                    onStartLevelUp = { questions -> screen = AppScreen.LevelUpTest(questions) },
                    onExtraPractice = { topic ->
                        screen = AppScreen.TopicPractice(
                            topic = topic,
                            daily = false,
                            exercises = ExerciseEngine.practiceSet(topic.id, ProgressStore.translateLang(context)).shuffled()
                        )
                    },
                    onStartReview = { topics -> screen = AppScreen.Review(topics) },
                    onOpenVerbSettings = { screen = AppScreen.VerbSettings },
                    onOpenEssay = { screen = AppScreen.Essay },
                    textScale = textScale,
                    onTextScale = { v ->
                        textScale = v
                        TextScale.set(context, v)
                    }
                )

                is AppScreen.Placement -> PlacementScreen(
                    onFinished = { res ->
                        ProgressStore.markPlacement(res.level, context)
                        goHome()
                    },
                    onExit = { goHome() }
                )

                is AppScreen.Lesson -> LessonScreen(
                    topic = current.topic,
                    onExit = { goHome() },
                    onStartPractice = { topic ->
                        screen = AppScreen.TopicPractice(
                            topic = topic,
                            daily = true,
                            exercises = ExerciseEngine.practiceSet(topic.id, ProgressStore.translateLang(context))
                        )
                    }
                )

                is AppScreen.TopicPractice -> ExerciseRunner(
                    title = current.topic.title,
                    subtitle = if (current.daily)
                        "Practice on the rule you just learned \u2014 the AI judge grades your writing fairly, and mistakes just mean more teaching."
                    else
                        "Extra practice \u2014 hints help you learn; nothing is at stake.",
                    exercises = current.exercises,
                    passFraction = 0.0,
                    showHints = true,
                    passMessage = "Good work \u2014 spaced reviews are scheduled so this rule stays in your memory.",
                    failMessage = "Good work \u2014 every mistake you made is now a scheduled review, and the rule will be taught again.",
                    onDone = { results, _, _ ->
                        ProgressStore.recordAnswers(context, results)
                        if (current.daily) {
                            ProgressStore.recordLesson(context, current.topic.id)
                        } else {
                            ProgressStore.recordStreakOnly(context)
                        }
                        goHome()
                    },
                    onExit = { goHome() }
                )

                is AppScreen.LevelUpTest -> QuizScreen(
                    title = "Level-up test",
                    questions = current.questions,
                    subtitle = "Hard questions \u2014 you need 6 of 8 to move up.",
                    passFraction = LEVEL_UP_PASS_FRACTION,
                    showHints = false,
                    onDone = { _, _, answers ->
                        if (answers.count { it } >= LEVEL_UP_MIN_CORRECT) {
                            ProgressStore.levelUp(context)
                        }
                        goHome()
                    },
                    onExit = { goHome() }
                )

                is AppScreen.Review -> ReviewScreen(
                    topics = current.topics,
                    onFinished = { goHome() }
                )

                AppScreen.VerbSettings -> VerbSettingsScreen(
                    onExit = { goHome() }
                )

                AppScreen.Essay -> EssayScreen(
                    onExit = { screen = AppScreen.Home }
                )
                AppScreen.VerbExercise -> VerbExerciseScreen(
                    onDone = { goHome() },
                    onExit = { goHome() }
                )
            }
        }
        }
    }
}

private const val LEVEL_UP_MIN_CORRECT = 6
private const val LEVEL_UP_PASS_FRACTION = 6.0 / 8

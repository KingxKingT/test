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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.grammardaily.data.Placement
import com.example.grammardaily.data.ProgressStore
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
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        ReminderWorker.schedule(context.applicationContext)
        VerbRotationWorker.schedule(context.applicationContext)
    }

    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
    var homeRefresh by remember { mutableIntStateOf(0) }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val current = screen) {
                is AppScreen.Home -> HomeScreen(
                    refresh = homeRefresh,
                    onStartPlacement = { screen = AppScreen.Placement },
                    onOpenLesson = { topic -> screen = AppScreen.Lesson(topic) },
                    onStartLevelUp = { questions -> screen = AppScreen.LevelUpTest(questions) },
                    onExtraPractice = { topic ->
                        screen = AppScreen.TopicPractice(
                            topic = topic,
                            daily = false,
                            exercises = ExerciseEngine.practiceSet(topic.id).shuffled()
                        )
                    },
                    onStartReview = { topics -> screen = AppScreen.Review(topics) },
                    onOpenVerbSettings = { screen = AppScreen.VerbSettings }
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
                            exercises = ExerciseEngine.practiceSet(topic.id)
                        )
                    }
                )

                is AppScreen.TopicPractice -> ExerciseRunner(
                    title = current.topic.title,
                    subtitle = if (current.daily)
                        "Pass 4 of 5 and the spaced reviews for this lesson begin."
                    else
                        "Extra practice \u2014 hints help you learn; nothing is at stake.",
                    exercises = current.exercises,
                    passFraction = LESSON_PASS_FRACTION,
                    showHints = true,
                    passMessage = "Passed \u2014 spaced reviews will keep this rule alive in your memory.",
                    failMessage = "Not passed \u2014 no problem. The rule and the right answers were shown; try again tomorrow or from the weak-spots card.",
                    onDone = { results, _, _ ->
                        ProgressStore.recordAnswers(context, results)
                        if (current.daily &&
                            results.count { it.correct }.toDouble() /
                            results.size.coerceAtLeast(1) >= LESSON_PASS_FRACTION
                        ) {
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

                AppScreen.VerbExercise -> VerbExerciseScreen(
                    onDone = { goHome() },
                    onExit = { goHome() }
                )
            }
        }
    }
}

private const val LESSON_PASS_FRACTION = 4.0 / 5
private const val LEVEL_UP_MIN_CORRECT = 6
private const val LEVEL_UP_PASS_FRACTION = 6.0 / 8

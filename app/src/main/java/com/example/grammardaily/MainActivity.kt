package com.example.grammardaily

import android.Manifest
import android.os.Build
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.grammardaily.data.Placement
import com.example.grammardaily.data.ProgressStore
import com.example.grammardaily.data.QuestionBank
import com.example.grammardaily.notify.ReminderWorker
import com.example.grammardaily.ui.AppScreen
import com.example.grammardaily.ui.HomeScreen
import com.example.grammardaily.ui.LessonScreen
import com.example.grammardaily.ui.QuizScreen
import com.example.grammardaily.ui.ReviewScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        ReminderWorker.schedule(context.applicationContext)
    }

    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
    var homeRefresh by remember { mutableIntStateOf(0) }

    fun goHome() {
        screen = AppScreen.Home
        homeRefresh++
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
                            questions = QuestionBank.byTopic(topic.id).shuffled()
                        )
                    },
                    onStartReview = { topics -> screen = AppScreen.Review(topics) }
                )

                is AppScreen.Placement -> QuizScreen(
                    title = "Full grammar test",
                    questions = remember { Placement.testQuestions() },
                    subtitle = "Trap answers included \u2014 answer honestly to land at the level where you will actually learn.",
                    passFraction = 1.0,
                    showHints = false,
                    onDone = { _, _, answers ->
                        ProgressStore.markPlacement(Placement.levelFromAnswers(answers), context)
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
                            questions = QuestionBank.byTopic(topic.id)
                                .take(LESSON_QUESTIONS)
                                .shuffled()
                        )
                    }
                )

                is AppScreen.TopicPractice -> QuizScreen(
                    title = current.topic.title,
                    questions = current.questions,
                    subtitle = if (current.daily)
                        "Pass 4 of 5 and the spaced reviews for this lesson begin."
                    else
                        "Extra practice \u2014 hints help you learn; nothing is at stake.",
                    passFraction = LESSON_PASS_FRACTION,
                    showHints = true,
                    onDone = { _, total, answers ->
                        if (current.daily && total > 0 &&
                            answers.count { it }.toDouble() / total >= LESSON_PASS_FRACTION
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
            }
        }
    }
}

private const val LESSON_QUESTIONS = 5
private const val LESSON_PASS_FRACTION = 4.0 / 5
private const val LEVEL_UP_MIN_CORRECT = 6
private const val LEVEL_UP_PASS_FRACTION = 6.0 / 8

package com.example.grammardaily

import android.Manifest
import android.content.Context
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
import com.example.grammardaily.data.GrammarCurriculum
import com.example.grammardaily.data.Placement
import com.example.grammardaily.data.ProgressStore
import com.example.grammardaily.notify.ReminderWorker
import com.example.grammardaily.ui.AppScreen
import com.example.grammardaily.ui.HomeScreen
import com.example.grammardaily.ui.LessonScreen
import com.example.grammardaily.ui.QuizScreen

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
                            questions = GrammarCurriculum.questionsForTopic(topic.id).shuffled()
                        )
                    }
                )

                is AppScreen.Placement -> QuizScreen(
                    title = "Full grammar test",
                    questions = remember { Placement.testQuestions() },
                    subtitle = "Answer without help \u2014 your score decides the level where training starts.",
                    onDone = { correct, _ ->
                        ProgressStore.markPlacement(Placement.levelForScore(correct), context)
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
                            questions = GrammarCurriculum.questionsForTopic(topic.id).shuffled()
                        )
                    }
                )

                is AppScreen.TopicPractice -> QuizScreen(
                    title = current.topic.title,
                    questions = remember(current.questions) { current.questions },
                    subtitle = "Get 2 of 3 right to master this lesson.",
                    onDone = { correct, total ->
                        recordPractice(context, current.topic.id, correct, total)
                        goHome()
                    },
                    onExit = { goHome() }
                )

                is AppScreen.LevelUpTest -> QuizScreen(
                    title = "Level-up test",
                    questions = remember(current.questions) { current.questions },
                    subtitle = "Answer at least 4 of 6 correctly to move up a level.",
                    onDone = { correct, total ->
                        if (correct * 2 >= total) {
                            ProgressStore.levelUp(context)
                        }
                        goHome()
                    },
                    onExit = { goHome() }
                )
            }
        }
    }
}

private fun recordPractice(context: Context, topicId: String, correct: Int, total: Int) {
    ProgressStore.recordQuizResult(context)
    if (total > 0 && correct * 3 >= total * 2) {
        ProgressStore.markTopicMastered(context, topicId)
        ProgressStore.markTodayLesson(context, topicId)
    }
}

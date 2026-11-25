package com.example.grammardaily.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.grammardaily.MainActivity
import com.example.grammardaily.data.GrammarCurriculum
import com.example.grammardaily.data.ProgressStore

class GrammarWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = ProgressStore.readState(context)
        val showPlacement = !state.placementDone
        val topic = state.todayLessonTopicId?.let { topicId ->
            GrammarCurriculum.topicsByLevel.values.flatten().firstOrNull { it.id == topicId }
        }
        val allDone = state.allMasteredFinished

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.background)
                        .clickable(actionStartActivity<MainActivity>())
                        .padding(10.dp),
                    horizontalAlignment = Alignment.Horizontal.Start,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                        Text(
                            text = if (showPlacement) "Grammar Daily" else "English ${state.level.code} \u00b7 ${state.level.title}",
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                        if (state.streakDays > 0) {
                            Spacer(modifier = GlanceModifier.width(6.dp))
                            Text(
                                text = "\ud83d\udd25${state.streakDays}",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Spacer(modifier = GlanceModifier.height(3.dp))
                    val line = when {
                        showPlacement -> "Tap to take the grammar test and find your level"
                        allDone -> "All lessons mastered \u2014 keep reading real English!"
                        state.todayLessonDone -> state.lessonDoneTopicId?.let { doneId ->
                            GrammarCurriculum.topicsByLevel.values.flatten().firstOrNull { it.id == doneId }
                        }?.let { "Done today: ${it.title}" } ?: "Lesson done today \u2014 see you tomorrow!"
                        topic != null -> topic.tip
                        else -> "Keep practicing!"
                    }
                    Text(
                        text = line,
                        style = TextStyle(color = GlanceTheme.colors.onBackground),
                        maxLines = 3
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    val levelTopics = GrammarCurriculum.topicsByLevel[state.level].orEmpty()
                    val masteredCount = levelTopics.count { it.id in state.masteredTopicIds }
                    LinearProgressIndicator(
                        progress = if (levelTopics.isEmpty()) 0f else masteredCount.toFloat() / levelTopics.size,
                        modifier = GlanceModifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

class GrammarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = GrammarWidget()
}

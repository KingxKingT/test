package com.example.grammardaily.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.update
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.grammardaily.MainActivity
import com.example.grammardaily.data.VerbBank
import com.example.grammardaily.data.VerbEvent
import com.example.grammardaily.data.VerbStore
import com.example.grammardaily.notify.VerbRotationWorker

class VerbWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val descriptor = VerbStore.currentCard(context)
            ?: VerbStore.pickNextCard(context).also { VerbStore.setCurrentCard(context, it) }
        val parts = descriptor.split("|")
        val event = runCatching {
            VerbBank.event(parts[0], parts[1].toInt(), parts[2].toInt())
        }.getOrElse { VerbBank.event("go", 0, 0) }
        val known = VerbStore.knownCards(context).contains(descriptor)

        provideContent {
            GlanceTheme {
                Content(context, event, known)
            }
        }
    }

    @Composable
    private fun Content(
        context: Context,
        event: VerbEvent,
        known: Boolean
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("verb_card", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(10.dp)
                .clickable(actionStartActivity(intent)),
            verticalAlignment = Alignment.Vertical.Top
        ) {
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    text = "Daily verb \u00b7 ${event.verb}",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                Text(
                    text = if (known) "\u2714 known" else event.tenseName,
                    style = TextStyle(color = GlanceTheme.colors.secondary, fontSize = 11.sp)
                )
            }
            Text(
                text = event.formsLine,
                style = TextStyle(color = GlanceTheme.colors.secondary, fontSize = 11.sp)
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = event.main,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = event.useLine,
                style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 11.sp)
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = event.alt,
                style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 11.sp)
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Row(
                    modifier = GlanceModifier
                        .background(GlanceTheme.colors.primary)
                        .cornerRadius(8.dp)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .clickable(actionRunCallback<KnownVerbAction>()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u2714 I know this",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.width(8.dp))
                Row(
                    modifier = GlanceModifier
                        .background(GlanceTheme.colors.secondaryContainer)
                        .cornerRadius(8.dp)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .clickable(actionRunCallback<ShowAgainVerbAction>()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u21bb Show again",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSecondaryContainer,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

class KnownVerbAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: androidx.glance.action.ActionParameters) {
        val descriptor = VerbStore.currentCard(context) ?: return
        VerbStore.markKnown(context, descriptor)
        VerbStore.rotate(context)
        VerbWidget().update(context, glanceId)
        VerbRotationWorker.schedule(context)
    }
}

class ShowAgainVerbAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: androidx.glance.action.ActionParameters) {
        val descriptor = VerbStore.currentCard(context) ?: return
        VerbStore.markShowAgain(context, descriptor)
        VerbStore.rotate(context)
        VerbWidget().update(context, glanceId)
        VerbRotationWorker.schedule(context)
    }
}

class VerbWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = VerbWidget()
}

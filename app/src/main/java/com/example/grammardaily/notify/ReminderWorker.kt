package com.example.grammardaily.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.grammardaily.MainActivity
import com.example.grammardaily.R
import com.example.grammardaily.data.ProgressStore
import com.example.grammardaily.widget.GrammarWidget
import java.util.concurrent.TimeUnit

object Notifications {

    const val CHANNEL_ID = "grammar_reminders"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Grammar reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Small grammar reminders through the day"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun show(context: Context, title: String, message: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            10,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(101, notification)
    }
}

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        ProgressStore.reminderMessage(context)?.let { (title, message) ->
            Notifications.show(context, title, message)
        }
        GrammarWidget().updateAll(context)
        return Result.success()
    }

    companion object {
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<ReminderWorker>(2, TimeUnit.HOURS)
                .setInitialDelay(2, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "grammar-reminder",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

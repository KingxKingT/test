package com.example.grammardaily.notify

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.glance.appwidget.updateAll
import com.example.grammardaily.data.VerbStore
import com.example.grammardaily.widget.VerbWidget
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class VerbRotationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext
        val quiet = VerbStore.quietWindow(VerbStore.quietHours(app))
        val hour = LocalTime.now().hour
        if (quiet != null && quiet.first != quiet.second) {
            val (start, end) = quiet
            val inQuiet = if (start < end) hour >= start && hour < end else hour >= start || hour < end
            if (inQuiet) return Result.success()
        }
        val intervalMs = (24L * 60 * 60 * 1000) / VerbStore.timesPerDay(app)
        val last = VerbStore.lastRotation(app)
        val nowMs = System.currentTimeMillis()
        if (last > 0 && nowMs - last < intervalMs) return Result.success()
        VerbStore.rotate(app)
        VerbWidget().updateAll(app)
        return Result.success()
    }

    companion object {
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<VerbRotationWorker>(30, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "verb-card-rotation",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

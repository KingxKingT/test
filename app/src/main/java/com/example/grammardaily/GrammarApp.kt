package com.example.grammardaily

import android.app.Application
import com.example.grammardaily.notify.Notifications
import com.example.grammardaily.notify.ReminderWorker

class GrammarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notifications.createChannel(this)
        ReminderWorker.schedule(this)
    }
}

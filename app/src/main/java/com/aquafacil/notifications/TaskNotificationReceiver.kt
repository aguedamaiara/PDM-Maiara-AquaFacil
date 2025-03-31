package com.aquafacil.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("task_title") ?: return
        val message = intent.getStringExtra("message") ?: return

        NotificationHelper(context).showNotification(taskTitle, message)
    }
}
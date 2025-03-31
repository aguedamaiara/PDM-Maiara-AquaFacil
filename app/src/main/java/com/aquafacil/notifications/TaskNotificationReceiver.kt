package com.aquafacil.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TaskNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("task_title") ?: return
        val message = intent.getStringExtra("message") ?: return

        val isDaily = intent.getBooleanExtra("is_daily", false)
        NotificationHelper(context).showNotification(title, message, isDaily)

        Log.d("Notification", "Notificação disparada: $title - $message")
    }
}
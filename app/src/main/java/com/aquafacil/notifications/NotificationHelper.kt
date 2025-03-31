package com.aquafacil.notifications


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.aquafacil.MainActivity
import com.aquafacil.R

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID_DAILY = "daily_tasks_channel"
        const val CHANNEL_ID_WEEKLY = "weekly_tasks_channel"
        const val CHANNEL_ID_MONTHLY = "monthly_tasks_channel"
        private const val NOTIFICATION_GROUP = "com.aquafacil.TASKS_GROUP"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal para tarefas diárias
            val dailyChannel = NotificationChannel(
                CHANNEL_ID_DAILY,
                context.getString(R.string.daily_tasks_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.daily_tasks_channel_description)
                enableLights(true)
                enableVibration(true)
            }

            // Canal para tarefas semanais
            val weeklyChannel = NotificationChannel(
                CHANNEL_ID_WEEKLY,
                context.getString(R.string.weekly_tasks_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.weekly_tasks_channel_description)
                enableLights(true)
                enableVibration(true)
            }

            // Canal para tarefas mensais
            val monthlyChannel = NotificationChannel(
                CHANNEL_ID_MONTHLY,
                context.getString(R.string.monthly_tasks_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.monthly_tasks_channel_description)
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannels(
                listOf(dailyChannel, weeklyChannel, monthlyChannel)
            )
        }
    }

    fun showNotification(title: String, message: String, isDaily: Boolean = true) {
        // Determina o canal correto baseado no tipo de tarefa
        val channelId = when {
            title.contains("Alimentação", ignoreCase = true) -> CHANNEL_ID_DAILY
            title.contains("Verificação geral", ignoreCase = true) -> CHANNEL_ID_MONTHLY
            else -> CHANNEL_ID_WEEKLY
        }

        // Intent para abrir o app quando a notificação for clicada
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("from_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            title.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cria a notificação com todas as configurações
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(getNotificationIcon())
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        // Exibe a notificação
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.notify(title.hashCode(), notification)
    }

    private fun getNotificationIcon(): Int {
        return try {
            // Verifica se o ícone personalizado existe
            val resId = context.resources.getIdentifier(
                "ic_notification",
                "drawable",
                context.packageName
            )
            if (resId != 0) resId else R.drawable.ic_notification
        } catch (e: Exception) {
            R.drawable.ic_notification
        }
    }
}
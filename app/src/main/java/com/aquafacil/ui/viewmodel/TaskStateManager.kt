package com.aquafacil.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aquafacil.model.Aquarium
import com.aquafacil.model.Task
import com.aquafacil.notifications.TaskNotificationReceiver
import com.aquafacil.utils.CronogramaUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class TaskStateManager(private val viewModel: AquariumViewModel, private val context: Context) {
    // Armazena o timestamp de quando a tarefa foi completada
    private val completedTasks = mutableMapOf<String, Long>()
    private val taskPeriods = mutableMapOf<String, String>()
    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    companion object {
        private const val INTERVAL_DAY = AlarmManager.INTERVAL_DAY
        private const val INTERVAL_WEEK = AlarmManager.INTERVAL_DAY * 7
        private const val NOTIFICATION_HOUR = 9 // 9:00 AM
    }

    fun isTaskCompleted(taskId: String, frequency: String, currentDate: Date = Date()): Boolean {
        if (!completedTasks.containsKey(taskId)) return false

        val completionTime = completedTasks[taskId] ?: return false
        val completionPeriod = taskPeriods[taskId] ?: return false
        val currentPeriod = CronogramaUtils.getCurrentPeriodId(frequency, currentDate)

        if (currentPeriod != completionPeriod) {
            completedTasks.remove(taskId)
            taskPeriods.remove(taskId)
            return false
        }

        return when (frequency) {
            "Diária" -> !isNewDay(completionTime, currentDate)
            "Semanal", "Bi-Semanal", "10 Dias" -> !isNewWeek(completionTime, currentDate)
            "Mensal" -> !isNewMonth(completionTime, currentDate)
            else -> true
        }
    }

    fun toggleTaskCompletion(task: Task, aquariumName: String) {
        val currentTime = System.currentTimeMillis()
        val currentPeriod = CronogramaUtils.getCurrentPeriodId(task.frequency, Date())

        if (isTaskCompleted(task.id, task.frequency)) {
            completedTasks.remove(task.id)
            taskPeriods.remove(task.id)
            cancelTaskNotification(task)
            Log.d("TaskState", "Tarefa marcada como não concluída: ${task.title}")
        } else {
            completedTasks[task.id] = currentTime
            taskPeriods[task.id] = currentPeriod
            scheduleTaskNotification(task, aquariumName)
            Log.d("TaskState", "Tarefa marcada como concluída: ${task.title}")
        }
        saveCompletedTasks()
    }

     fun scheduleTaskNotification(task: Task, aquariumName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra("task_title", "${aquariumName}: ${task.title}")
            putExtra("message", "Lembrete: ${task.title} está agendado para hoje!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9) // Notificar às 9:00 AM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            // Se já passou das 9:00 hoje, agenda para amanhã
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        when {
            task.title.contains("Troca parcial de água") -> {
                // Semanal (toda segunda-feira)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
                )
            }
            task.title.contains("Verificação de salinidade") -> {
                // Semanal (toda segunda-feira)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
                )
            }
            task.title.contains("Alimentação") -> {
                // Diária (duas vezes ao dia)
                val morningIntent = intent.apply {
                    putExtra("message", "Hora de alimentar os peixes (1ª vez)")
                }
                val afternoonIntent = intent.apply {
                    putExtra("message", "Hora de alimentar os peixes (2ª vez)")
                }

                // Manhã (9:00)
                val morningPending = PendingIntent.getBroadcast(
                    context,
                    task.id.hashCode() + 1, // ID diferente para a segunda notificação
                    morningIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    morningPending
                )

                // Tarde (15:00)
                calendar.set(Calendar.HOUR_OF_DAY, 15)
                val afternoonPending = PendingIntent.getBroadcast(
                    context,
                    task.id.hashCode() + 2,
                    afternoonIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    afternoonPending
                )
            }
            task.title.contains("Monitoramento") -> {
                // Semanal (toda segunda-feira)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
                )
            }
            task.title.contains("Limpeza do filtro") -> {
                // Semanal (toda segunda-feira)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
                )
            }
            task.title.contains("Verificação geral") -> {
                // Mensal (dia 30)
                calendar.set(Calendar.DAY_OF_MONTH, 30)
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.MONTH, 1)
                }
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 30, // Aproximadamente 1 mês
                    pendingIntent
                )
            }
        }

        Log.d("Notification", "Notificação agendada para ${task.title} em ${calendar.time}")
    }

    private fun cancelTaskNotification(task: Task) {
        val intent = Intent(context, TaskNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        Log.d("Notification", "Notificação cancelada para tarefa: ${task.title}")
    }

    private fun isNewDay(oldTime: Long, currentDate: Date): Boolean {
        val calOld = Calendar.getInstance().apply { timeInMillis = oldTime }
        val calNow = Calendar.getInstance().apply { time = currentDate }
        return calOld.get(Calendar.YEAR) != calNow.get(Calendar.YEAR) ||
                calOld.get(Calendar.DAY_OF_YEAR) != calNow.get(Calendar.DAY_OF_YEAR)
    }

    private fun isNewWeek(oldTime: Long, currentDate: Date): Boolean {
        val calOld = Calendar.getInstance().apply { timeInMillis = oldTime }
        val calNow = Calendar.getInstance().apply { time = currentDate }
        return calOld.get(Calendar.YEAR) != calNow.get(Calendar.YEAR) ||
                calOld.get(Calendar.WEEK_OF_YEAR) != calNow.get(Calendar.WEEK_OF_YEAR)
    }

    private fun isNewMonth(oldTime: Long, currentDate: Date): Boolean {
        val calOld = Calendar.getInstance().apply { timeInMillis = oldTime }
        val calNow = Calendar.getInstance().apply { time = currentDate }
        return calOld.get(Calendar.YEAR) != calNow.get(Calendar.YEAR) ||
                calOld.get(Calendar.MONTH) != calNow.get(Calendar.MONTH)
    }

    private fun saveCompletedTasks() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).child("completedTasks").setValue(completedTasks)
        database.child("users").child(userId).child("taskPeriods").setValue(taskPeriods)
    }

    fun loadCompletedTasks(userId: String, onComplete: () -> Unit = {}) {
        val tasksRef = database.child("users").child(userId).child("completedTasks")

        tasksRef.get().addOnSuccessListener { snapshot ->
            completedTasks.clear()

            for (doc in snapshot.children) {
                val taskId = doc.child("taskId").getValue(String::class.java)
                val completedTime = doc.child("completedDate").getValue(Long::class.java) ?: System.currentTimeMillis()

                if (taskId != null) {
                    completedTasks[taskId] = completedTime
                }
            }

            val periodsRef = database.child("users").child(userId).child("taskPeriods")
            periodsRef.get().addOnSuccessListener { periodsSnapshot ->
                taskPeriods.clear()

                for (doc in periodsSnapshot.children) {
                    val taskId = doc.child("taskId").getValue(String::class.java)
                    val period = doc.child("period").getValue(String::class.java)

                    if (taskId != null && period != null) {
                        taskPeriods[taskId] = period
                    }
                }

                onComplete()
            }.addOnFailureListener {
                onComplete()
            }
        }.addOnFailureListener {
            onComplete()
        }
    }

    fun clearAllTasks() {
        completedTasks.clear()
        taskPeriods.clear()
    }

    fun rescheduleAllNotifications(aquariums: List<Aquarium>) {
        aquariums.forEach { aquarium ->
            val tasks = CronogramaUtils.gerarCronograma(aquarium, Date())
            tasks.forEach { task ->
                if (!isTaskCompleted(task.id, task.frequency)) {
                    scheduleTaskNotification(task, aquarium.name)
                }
            }
        }
    }
}
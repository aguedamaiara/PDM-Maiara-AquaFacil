package com.aquafacil.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

    // Armazena o período em que a tarefa foi completada (ex: "2023-45" para semana 45)
    private val taskPeriods = mutableMapOf<String, String>()

    // Referência ao banco de dados do Firebase
    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    fun isTaskCompleted(taskId: String, frequency: String, currentDate: Date = Date()): Boolean {
        // Se não está no mapa, nunca foi completada
        if (!completedTasks.containsKey(taskId)) return false

        val completionTime = completedTasks[taskId] ?: return false
        val completionPeriod = taskPeriods[taskId] ?: return false

        // Obtém o período atual baseado na data fornecida
        val currentPeriod = CronogramaUtils.getCurrentPeriodId(frequency, currentDate)

        // Se mudou o período, remove a tarefa e retorna não completada
        if (currentPeriod != completionPeriod) {
            completedTasks.remove(taskId)
            taskPeriods.remove(taskId)
            return false
        }

        // Verifica se o período específico expirou
        return when (frequency) {
            "Diária" -> !isNewDay(completionTime, currentDate)
            "Semanal", "Bi-Semanal", "10 Dias" -> !isNewWeek(completionTime, currentDate)
            "Mensal" -> !isNewMonth(completionTime, currentDate)
            else -> true // Para frequências desconhecidas, mantém como completada
        }
    }

    fun toggleTaskCompletion(task: Task, aquariumName: String) {
        val currentTime = System.currentTimeMillis()
        val currentPeriod = CronogramaUtils.getCurrentPeriodId(task.frequency, Date())

        if (isTaskCompleted(task.id, task.frequency)) {
            // Se já está completada, desmarca
            completedTasks.remove(task.id)
            taskPeriods.remove(task.id)
            // Cancela a notificação
            cancelTaskNotification(task)
        } else {
            // Se não está completada, marca com timestamp e período atual
            completedTasks[task.id] = currentTime
            taskPeriods[task.id] = currentPeriod
            // Agenda a notificação
            scheduleTaskNotification(task, aquariumName)
        }
        saveCompletedTasks()
    }

    private fun scheduleTaskNotification(task: Task, aquariumName: String) {
        // Calcular o horário para a notificação (1 hora antes da tarefa)
        val alarmTime = Calendar.getInstance().apply {
            time = Date() // Você pode ajustar isso para a data real da tarefa
            add(Calendar.HOUR_OF_DAY, 1) // Notificar em 1 hora (para teste)
        }.timeInMillis

        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra("task_title", "${aquariumName}: ${task.title}")
            putExtra("message", "Esta tarefa está agendada para hoje!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )
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

        val tasksRef = database.child("users").child(userId).child("completedTasks")
        tasksRef.setValue(completedTasks)

        val periodsRef = database.child("users").child(userId).child("taskPeriods")
        periodsRef.setValue(taskPeriods)
    }

    fun loadCompletedTasks(userId: String, onComplete: () -> Unit = {}) {
        val tasksRef = database.child("users").child(userId).child("completedTasks")
        tasksRef.get().addOnSuccessListener { snapshot ->
            val tasks = snapshot.getValue(object :
                com.google.firebase.database.GenericTypeIndicator<Map<String, Long>>() {}) ?: emptyMap()
            completedTasks.clear()
            completedTasks.putAll(tasks)

            val periodsRef = database.child("users").child(userId).child("taskPeriods")
            periodsRef.get().addOnSuccessListener { periodsSnapshot ->
                val periods = periodsSnapshot.getValue(object :
                    com.google.firebase.database.GenericTypeIndicator<Map<String, String>>() {}) ?: emptyMap()
                taskPeriods.clear()
                taskPeriods.putAll(periods)
                onComplete()
            }.addOnFailureListener {
                onComplete()
            }
        }.addOnFailureListener {
            onComplete()
        }
    }

    // Função auxiliar para limpar todas as tarefas (útil para testes)
    fun clearAllTasks() {
        completedTasks.clear()
        taskPeriods.clear()
    }
}
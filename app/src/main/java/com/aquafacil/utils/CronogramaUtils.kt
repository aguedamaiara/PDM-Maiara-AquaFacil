package com.aquafacil.utils

import com.aquafacil.model.Aquarium
import com.aquafacil.model.AquariumType
import com.aquafacil.model.Task
import java.text.SimpleDateFormat
import java.util.*

object CronogramaUtils {

    fun gerarCronograma(aquarium: Aquarium, selectedDate: Date): List<Task> {
        val tasks = mutableListOf<Task>()
        val quantidadePeixes = aquarium.fishQuantity

        fun createTask(title: String, frequency: String): Task {
            val (todayText, nextText) = getNextDates(frequency, selectedDate)
            return Task(
                id = "${aquarium.id}_${title.hashCode()}_${getCurrentPeriodId(frequency, selectedDate)}",
                title = "$title - $todayText",
                dates = nextText,
                frequency = frequency
            )
        }

        when (aquarium.type) {
            AquariumType.FRESHWATER -> {
                tasks.add(createTask("Troca parcial de água", calcularFrequenciaTroca(aquarium.size)))
            }
            AquariumType.SALTWATER -> {
                tasks.add(createTask("Troca parcial de água", calcularFrequenciaTroca(aquarium.size)))
                tasks.add(createTask("Verificação de salinidade", "Semanal"))
            }
            else -> {
                tasks.add(Task(
                    id = "${aquarium.id}_unknown",
                    title = "Tipo de aquário desconhecido",
                    dates = "Verifique as configurações",
                    frequency = ""
                ))
            }
        }

        if (quantidadePeixes.isNotEmpty() && quantidadePeixes != "0") {
            tasks.add(Task(
                id = "${aquarium.id}_feeding_${getCurrentPeriodId("Diária", selectedDate)}",
                title = "Alimentação dos peixes - ${calcularFrequenciaAlimentacao(quantidadePeixes)}",
                dates = "",
                frequency = "Diária"
            ))

            tasks.add(createTask(
                "Monitoramento da qualidade da água",
                calcularFrequenciaMonitoramento(quantidadePeixes)
            ))

            if (quantidadePeixes == "5-10" || quantidadePeixes == "Mais de 10") {
                tasks.add(createTask("Verificação de oxigênio dissolvido", "Semanal"))
            }
        }

        if (aquarium.isPlanted) {
            tasks.add(createTask("Fertilização das plantas aquáticas", "Semanal"))
            tasks.add(createTask("Podas e manutenção das plantas", "Bi-Semanal"))

            if (quantidadePeixes == "5-10" || quantidadePeixes == "Mais de 10") {
                tasks.add(createTask("Verificação de níveis de CO2", "Semanal"))
            }
        }

        if (aquarium.hasEquipment) {
            val frequenciaLimpeza = when (quantidadePeixes) {
                "Mais de 10" -> "Semanal"
                "5-10" -> "10 Dias"
                else -> "Bi-Semanal"
            }

            tasks.add(createTask("Limpeza do filtro", frequenciaLimpeza))
            tasks.add(createTask("Verificação geral dos equipamentos", "Mensal"))
        }

        return tasks
    }

    fun getNextDates(frequency: String, baseDate: Date): Pair<String, String> {
        val calendar = Calendar.getInstance().apply { time = baseDate }
        val todayText = formatDate(calendar.time)

        return when {
            frequency.contains("Semanal") -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                Pair("Hoje ($todayText)", "Próxima: ${formatDate(calendar.time)}")
            }
            frequency.contains("Bi-Semanal") -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 2)
                Pair("Hoje ($todayText)", "Próxima: ${formatDate(calendar.time)}")
            }
            frequency.contains("10 Dias") -> {
                calendar.add(Calendar.DAY_OF_YEAR, 10)
                Pair("Hoje ($todayText)", "Próxima: ${formatDate(calendar.time)}")
            }
            frequency.contains("Mensal") -> {
                calendar.add(Calendar.MONTH, 1)
                Pair("Hoje ($todayText)", "Próxima: ${formatDate(calendar.time)}")
            }
            else -> Pair(frequency, "")
        }
    }

    fun getCurrentPeriodId(frequency: String, date: Date): String {
        val cal = Calendar.getInstance().apply { time = date }
        return when (frequency) {
            "Diária" -> "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
            "Semanal", "Bi-Semanal", "10 Dias" -> "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.WEEK_OF_YEAR)}"
            "Mensal" -> "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}"
            else -> "${date.time}" // Usa timestamp como fallback
        }
    }

    fun formatDate(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }

    fun calcularFrequenciaTroca(tamanho: Double): String {
        return when {
            tamanho <= 50 -> "Semanal"
            tamanho <= 100 -> "Bi-Semanal"
            else -> "Mensal"
        }
    }

    fun calcularFrequenciaAlimentacao(quantidadePeixes: String): String {
        return when (quantidadePeixes) {
            "1" -> "1 vez ao dia"
            "1-5" -> "1-2 vezes ao dia"
            "5-10" -> "2 vezes ao dia"
            "Mais de 10" -> "2-3 vezes ao dia"
            else -> "Nenhuma alimentação programada"
        }
    }

    fun calcularFrequenciaMonitoramento(quantidadePeixes: String): String {
        return when (quantidadePeixes) {
            "1" -> "Bi-Semanal"
            "1-5" -> "Semanal"
            "5-10" -> "2 vezes por semana"
            "Mais de 10" -> "3 vezes por semana"
            else -> "Mensal"
        }
    }
}
// Em CronogramaUtils.kt
fun calculateNotificationTime(task: Task, baseDate: Date): Long {
    val calendar = Calendar.getInstance().apply { time = baseDate }

    // Ajuste conforme a frequência da tarefa
    when (task.frequency) {
        "Diária" -> calendar.set(Calendar.HOUR_OF_DAY, 9) // 9 AM
        "Semanal" -> {
            calendar.set(Calendar.HOUR_OF_DAY, 9)
            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            }
        }
        "Mensal" -> {
            calendar.set(Calendar.HOUR_OF_DAY, 9)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    return calendar.timeInMillis
}
package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aquafacil.model.Aquarium
import com.aquafacil.model.AquariumType
import com.aquafacil.model.getDisplayName
import com.aquafacil.ui.viewmodel.AquariumViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CronogramaScreen(aquariumViewModel: AquariumViewModel) {
    // Estados observáveis
    val aquariums by aquariumViewModel.aquariums.observeAsState(initial = emptyList())
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }

    // Carrega os aquários quando a tela for exibida
    LaunchedEffect(Unit) {
        aquariumViewModel.loadAquariums()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronograma de Manutenção") },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Selecionar data")
                    }
                    IconButton(onClick = { showFilters = true }) {
                        Icon(Icons.Default.FilterAlt, contentDescription = "Filtros")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { selectedDate = Date() }, // Volta para hoje
                icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Hoje") },
                text = { Text("Hoje") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mostra a data selecionada
            Text(
                text = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(selectedDate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (aquariums.isEmpty()) {
                Text("Nenhum aquário encontrado.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(aquariums) { aquarium ->
                        val cronograma = gerarCronograma(aquarium, selectedDate).map { task ->
                            // Atualiza o estado completado com base no ViewModel
                            task.copy(completed = aquariumViewModel.taskStateManager.isTaskCompleted(task.id))
                        }

                        if (cronograma.isNotEmpty()) {
                            // Cabeçalho do aquário
                            Text(
                                text = "${aquarium.name} - ${aquarium.type.getDisplayName()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Itens do cronograma
                            cronograma.forEach { atividade ->
                                CronogramaItem(
                                    atividade = atividade,
                                    onCheckedChange = { checked ->
                                        // Atualiza o estado no ViewModel
                                        aquariumViewModel.taskStateManager.toggleTaskCompletion(atividade.id)
                                    }
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp))
                        }
                    }
                }
            }
        }
    }

    // Diálogo para seleção de data
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.time)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Date(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Diálogo de filtros (implementação básica)
    if (showFilters) {
        AlertDialog(
            onDismissRequest = { showFilters = false },
            title = { Text("Filtrar Tarefas") },
            text = { Text("Filtros serão implementados aqui") },
            confirmButton = {
                TextButton(onClick = { showFilters = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun CronogramaItem(
    atividade: Task,
    onCheckedChange: (Boolean) -> Unit
) {
    var checkedState by remember { mutableStateOf(atividade.completed) }

    LaunchedEffect(atividade.completed) {
        checkedState = atividade.completed
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (checkedState) 1.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (checkedState) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkedState,
                onCheckedChange = {
                    checkedState = it
                    onCheckedChange(it)
                },
                modifier = Modifier.padding(end = 16.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = atividade.title,
                    style = if (checkedState) {
                        MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        MaterialTheme.typography.bodyLarge
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (atividade.dates.isNotEmpty()) {
                    Text(
                        text = atividade.dates,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (checkedState) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                }
            }
        }
    }
}

// Modelo de dados para tarefas
data class Task(
    val id: String,
    val title: String,
    val dates: String,
    val completed: Boolean = false
)

// Função para gerar cronograma com suporte a data selecionada
fun gerarCronograma(aquarium: Aquarium, selectedDate: Date): List<Task> {
    val tasks = mutableListOf<Task>()
    val quantidadePeixes = aquarium.fishQuantity
    val today = Calendar.getInstance().apply { time = selectedDate }

    // Função auxiliar para criar tarefas
    fun createTask(title: String, frequency: String): Task {
        val (todayText, nextText) = getNextDates(frequency, selectedDate)
        return Task(
            id = "${aquarium.id}_${title.hashCode()}",
            title = "$title - $todayText",
            dates = nextText
        )
    }

    // Tarefas baseadas no tipo de aquário
    when (aquarium.type) {
        AquariumType.FRESHWATER -> {
            tasks.add(createTask("Troca parcial de água", calcularFrequenciaTroca(aquarium.size)))
        }
        AquariumType.SALTWATER -> {
            tasks.add(createTask("Troca parcial de água", calcularFrequenciaTroca(aquarium.size)))
            tasks.add(createTask("Verificação de salinidade", "Semanalmente"))
        }
        else -> {
            tasks.add(Task(
                id = "${aquarium.id}_unknown",
                title = "Tipo de aquário desconhecido",
                dates = "Verifique as configurações"
            ))
        }
    }

    // Tarefas baseadas na quantidade de peixes
    if (quantidadePeixes.isNotEmpty() && quantidadePeixes != "0") {
        tasks.add(Task(
            id = "${aquarium.id}_feeding",
            title = "Alimentação dos peixes - ${calcularFrequenciaAlimentacao(quantidadePeixes)}",
            dates = ""
        ))

        tasks.add(createTask(
            "Monitoramento da qualidade da água",
            calcularFrequenciaMonitoramento(quantidadePeixes)
        ))

        if (quantidadePeixes == "5-10" || quantidadePeixes == "Mais de 10") {
            tasks.add(createTask("Verificação de oxigênio dissolvido", "Semanalmente"))
        }
    }

    // Tarefas para aquários plantados
    if (aquarium.isPlanted) {
        tasks.add(createTask("Fertilização das plantas aquáticas", "Semanalmente"))
        tasks.add(createTask("Podas e manutenção das plantas", "A cada 2 semanas"))

        if (quantidadePeixes == "5-10" || quantidadePeixes == "Mais de 10") {
            tasks.add(createTask("Verificação de níveis de CO2", "Semanalmente"))
        }
    }

    // Manutenção de equipamentos
    if (aquarium.hasEquipment) {
        val frequenciaLimpeza = when (quantidadePeixes) {
            "Mais de 10" -> "Semanalmente"
            "5-10" -> "A cada 10 dias"
            else -> "A cada 2 semanas"
        }

        tasks.add(createTask("Limpeza do filtro", frequenciaLimpeza))
        tasks.add(createTask("Verificação geral dos equipamentos", "Mensalmente"))
    }

    return tasks
}

// Função para calcular datas (atualizada para trabalhar com qualquer data)
fun getNextDates(frequency: String, baseDate: Date): Pair<String, String> {
    val calendar = Calendar.getInstance().apply { time = baseDate }
    val todayText = formatDate(calendar.time)

    return when {
        frequency.contains("Semanalmente") -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            val nextText = formatDate(calendar.time)
            Pair("Hoje ($todayText)", "Próxima: $nextText")
        }
        frequency.contains("A cada 2 semanas") -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 2)
            val nextText = formatDate(calendar.time)
            Pair("Hoje ($todayText)", "Próxima: $nextText")
        }
        frequency.contains("A cada 10 dias") -> {
            calendar.add(Calendar.DAY_OF_YEAR, 10)
            val nextText = formatDate(calendar.time)
            Pair("Hoje ($todayText)", "Próxima: $nextText")
        }
        frequency.contains("Mensalmente") -> {
            calendar.add(Calendar.MONTH, 1)
            val nextText = formatDate(calendar.time)
            Pair("Hoje ($todayText)", "Próxima: $nextText")
        }
        else -> Pair(frequency, "")
    }
}
// Função para formatar datas
fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(date)
}

// Função para calcular a frequência de troca de água baseada no tamanho do aquário
fun calcularFrequenciaTroca(tamanho: Double): String {
    return when {
        tamanho <= 50 -> "Semanal (20-30%)"
        tamanho <= 100 -> "A cada 2 semanas (15-20%)"
        else -> "A cada 3 semanas (10-15%)"
    }
}

// Função para calcular a frequência de alimentação baseada na quantidade de peixes
fun calcularFrequenciaAlimentacao(quantidadePeixes: String): String {
    return when (quantidadePeixes) {
        "1" -> "1 vez ao dia (pequena porção)"
        "1-5" -> "1-2 vezes ao dia (porções moderadas)"
        "5-10" -> "2 vezes ao dia (manhã e tarde)"
        "Mais de 10" -> "2-3 vezes ao dia (pequenas porções para evitar sobras)"
        else -> "Nenhuma alimentação programada"
    }
}

// Função para calcular a frequência de monitoramento da água
fun calcularFrequenciaMonitoramento(quantidadePeixes: String): String {
    return when (quantidadePeixes) {
        "1" -> "A cada 10-14 dias"
        "1-5" -> "Semanalmente"
        "5-10" -> "2 vezes por semana"
        "Mais de 10" -> "3 vezes por semana ou diariamente para aquários muito populosos"
        else -> "Monitoramento básico mensal"
    }
}
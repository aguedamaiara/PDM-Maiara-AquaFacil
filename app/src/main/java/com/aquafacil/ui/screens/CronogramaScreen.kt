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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aquafacil.model.Aquarium
import com.aquafacil.model.AquariumType
import com.aquafacil.model.Task
import com.aquafacil.model.getDisplayName
import com.aquafacil.notifications.NotificationHelper
import com.aquafacil.ui.viewmodel.AquariumViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.aquafacil.utils.CronogramaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CronogramaScreen(aquariumViewModel: AquariumViewModel) {
    // Estados observáveis
    val aquariums by aquariumViewModel.aquariums.observeAsState(initial = emptyList())
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }

    // Obter o contexto atual
    val context = LocalContext.current

    /*// TESTE RÁPIDO DE NOTIFICAÇÃO (adicionar esta parte)
    LaunchedEffect(Unit) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(
            "Teste de Notificação",
            "Esta é uma notificação de teste do AquaFacil!"
        )
    }*/
    
    // Carrega os aquários e tarefas completas quando a tela for exibida
    LaunchedEffect(Unit) {
        aquariumViewModel.loadAquariums()
        aquariumViewModel.getCurrentUserId()?.let { userId ->
            aquariumViewModel.taskStateManager.loadCompletedTasks(userId)
        }
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
                onClick = {
                    selectedDate = Date()
                    // Recarrega as tarefas ao voltar para hoje
                    aquariumViewModel.getCurrentUserId()?.let { userId ->
                        aquariumViewModel.taskStateManager.loadCompletedTasks(userId)
                    }
                },
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
                LazyColumn {
                    items(aquariums) { aquarium ->
                        val cronograma = CronogramaUtils.gerarCronograma(aquarium, selectedDate)
                            .map { task ->
                                task.copy(
                                    completed = aquariumViewModel.taskStateManager.isTaskCompleted(
                                        task.id,
                                        task.frequency,
                                        selectedDate // Passe a data selecionada
                                    )
                                )
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
                                    onCheckedChange = { task, checked ->
                                        // Atualizado para passar o nome do aquário
                                        aquariumViewModel.taskStateManager.toggleTaskCompletion(
                                            task,
                                            aquarium.name
                                        )
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

    // Diálogo de filtros
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
    onCheckedChange: (Task, Boolean) -> Unit
) {
    var checkedState by remember { mutableStateOf(atividade.completed) }

    LaunchedEffect(atividade) {
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
                    onCheckedChange(atividade, it)
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
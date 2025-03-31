package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquafacil.model.Aquarium
import com.aquafacil.model.AquariumType
import com.aquafacil.model.getDisplayName
import com.aquafacil.ui.viewmodel.AquariumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CronogramaScreen(aquariumViewModel: AquariumViewModel) {
    // Observa a lista de aquários
    val aquariums by aquariumViewModel.aquariums.observeAsState(initial = emptyList())

    // Carrega os aquários quando a tela for exibida
    LaunchedEffect(Unit) {
        aquariumViewModel.loadAquariums()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cronograma de Manutenção") })
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
            Text(
                text = "Aqui estão os cronogramas dos seus aquários:",
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (aquariums.isEmpty()) {
                Text("Nenhum aquário encontrado.")
            } else {
                LazyColumn {
                    items(aquariums) { aquarium ->
                        Text(
                            text = "Cronograma para o aquário ${aquarium.name} - ${aquarium.type.getDisplayName()}",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val cronograma = gerarCronograma(aquarium)
                        cronograma.forEach { atividade ->
                            CronogramaItem(atividade)
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CronogramaItem(atividade: String) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            modifier = Modifier.padding(end = 16.dp)
        )

        Text(
            text = atividade,
            modifier = Modifier.weight(1f),
            style = if (isChecked) {
                MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                MaterialTheme.typography.bodyMedium
            }
        )
    }
}

fun gerarCronograma(aquarium: Aquarium): List<String> {
    val cronograma = mutableListOf<String>()

    // Definindo o cronograma com base no tipo de aquário e tamanho
    when (aquarium.type) {
        AquariumType.FRESHWATER  -> {
            cronograma.add("Troca parcial de água - ${calcularFrequenciaTroca(aquarium.size)}")
        }
        AquariumType.SALTWATER -> {
            cronograma.add("Troca parcial de água - ${calcularFrequenciaTroca(aquarium.size)}")
            cronograma.add("Verificação de salinidade - Semanalmente")
        }
        else -> {
            cronograma.add("Tipo de aquário desconhecido. Verifique as configurações.")
        }
    }

    // Adiciona tarefas relacionadas ao número de peixes com regras específicas
    val quantidadePeixes = aquarium.fishQuantity.toIntOrNull() ?: 0
    if (quantidadePeixes > 0) {
        cronograma.add("Alimentação dos peixes - ${calcularFrequenciaAlimentacao(quantidadePeixes)}")
        cronograma.add("Monitoramento da qualidade da água - ${calcularFrequenciaMonitoramento(quantidadePeixes)}")

        if (quantidadePeixes > 10) {
            cronograma.add("Verificação de oxigênio dissolvido - Diariamente")
        }
    }

    // Adiciona tarefas se o aquário for plantado
    if (aquarium.isPlanted) {
        cronograma.add("Fertilização das plantas aquáticas - Semanalmente")
        cronograma.add("Podas e manutenção das plantas - A cada 2 semanas")

        if (quantidadePeixes > 5) {
            cronograma.add("Verificação de CO2 - Semanalmente")
        }
    }

    // Adiciona tarefas se houver equipamentos
    if (aquarium.hasEquipment) {
        cronograma.add("Verificação e limpeza dos equipamentos - A cada 2 semanas")

        if (quantidadePeixes > 15) {
            cronograma.add("Limpeza do filtro - Semanalmente")
        }
    }

    return cronograma
}

// Novas funções para calcular frequências baseadas na quantidade de peixes
fun calcularFrequenciaAlimentacao(quantidadePeixes: Int): String {
    return when {
        quantidadePeixes <= 5 -> "1 vez ao dia"
        quantidadePeixes <= 10 -> "2 vezes ao dia (manhã e tarde)"
        quantidadePeixes <= 20 -> "3 vezes ao dia (pequenas porções)"
        else -> "4 vezes ao dia (pequenas porções)"
    }
}

fun calcularFrequenciaMonitoramento(quantidadePeixes: Int): String {
    return when {
        quantidadePeixes <= 5 -> "Semanalmente"
        quantidadePeixes <= 10 -> "2 vezes por semana"
        quantidadePeixes <= 20 -> "3 vezes por semana"
        else -> "Diariamente"
    }
}

fun calcularFrequenciaTroca(tamanho: Double): String {
    return when {
        tamanho <= 50 -> "Semanal"
        tamanho <= 100 -> "A cada 2 semanas"
        else -> "A cada 3 semanas"
    }
}

@Composable
fun AquariumScheduleCard(aquarium: Aquarium) {
    val cronograma = gerarCronograma(aquarium)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cabeçalho com informações do aquário
            Text(
                text = "Aquário ${aquarium.type.getDisplayName()} - ${aquarium.size}L",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de tarefas
            cronograma.forEach { atividade ->
                CronogramaItem(atividade)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
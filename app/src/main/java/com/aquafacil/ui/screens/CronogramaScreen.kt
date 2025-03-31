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
            TopAppBar(title = { Text("Aqui estão os cronogramas dos seus aquários:") })
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
//            Text(
//                text = "Aqui estão os cronogramas dos seus aquários:",
//                fontSize = 18.sp,
//                style = MaterialTheme.typography.titleMedium
//            )
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
    val quantidadePeixes = aquarium.fishQuantity

    // Tarefas baseadas no tipo de aquário
    when (aquarium.type) {
        AquariumType.FRESHWATER -> {
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

    // Tarefas baseadas na quantidade de peixes
    if (quantidadePeixes.isNotEmpty() && quantidadePeixes != "0") {
        cronograma.add("Alimentação dos peixes - ${calcularFrequenciaAlimentacao(quantidadePeixes)}")
        cronograma.add("Monitoramento da qualidade da água - ${calcularFrequenciaMonitoramento(quantidadePeixes)}")

        if (quantidadePeixes == "5-10" || quantidadePeixes == "Mais de 10") {
            cronograma.add("Verificação de oxigênio dissolvido - Semanalmente")
        }
    } else {
        cronograma.add("Nenhum peixe no aquário. Nenhuma alimentação necessária.")
    }

    // Tarefas para aquários plantados
    if (aquarium.isPlanted) {
        cronograma.add("Fertilização das plantas aquáticas - Semanalmente")
        cronograma.add("Podas e manutenção das plantas - A cada 2 semanas")

        if (quantidadePeixes == "5-10" || quantidadePeixes == "Mais de 10") {
            cronograma.add("Verificação de níveis de CO2 - Semanalmente")
        }
    }

    // Manutenção de equipamentos
    if (aquarium.hasEquipment) {
        val frequenciaLimpeza = when (quantidadePeixes) {
            "Mais de 10" -> "Semanalmente"
            "5-10" -> "A cada 10 dias"
            else -> "A cada 2 semanas"
        }
        cronograma.add("Limpeza do filtro - $frequenciaLimpeza")
        cronograma.add("Verificação geral dos equipamentos - Mensalmente")
    }

    return cronograma
}

// Função para calcular a frequência de alimentação com base na quantidade de peixes
fun calcularFrequenciaAlimentacao(quantidadePeixes: String): String {
    return when (quantidadePeixes) {
        "1" -> "1 vez ao dia (pequena porção)"
        "1-5" -> "1-2 vezes ao dia (porções moderadas)"
        "5-10" -> "2 vezes ao dia (manhã e tarde)"
        "Mais de 10" -> "2-3 vezes ao dia (pequenas porções para evitar sobras)"
        else -> "Nenhuma alimentação programada"
    }
}


fun calcularFrequenciaMonitoramento(quantidadePeixes: String): String {
    return when (quantidadePeixes) {
        "1" -> "A cada 10-14 dias"
        "1-5" -> "Semanalmente"
        "5-10" -> "2 vezes por semana"
        "Mais de 10" -> "3 vezes por semana ou diariamente para aquários muito populosos"
        else -> "Monitoramento básico mensal"
    }
}

// Mantemos a função de troca de água igual pois não depende da quantidade de peixes
fun calcularFrequenciaTroca(tamanho: Double): String {
    return when {
        tamanho <= 50 -> "Semanal (20-30%)"
        tamanho <= 100 -> "A cada 2 semanas (15-20%)"
        else -> "A cada 3 semanas (10-15%)"
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
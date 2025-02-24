package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentScreen(navController: NavController) {
    // Lista de equipamentos disponíveis
    val equipmentList = listOf("Filtro Canister", "Filtro Esponja", "Iluminação LED", "Sistema de CO₂", "Aquecedor", "Termômetro")

    // Estado para armazenar os equipamentos selecionados
    var selectedEquipment by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Equipamentos") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Selecione os equipamentos que utiliza:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Lista de equipamentos com checkboxes
            Column(modifier = Modifier.selectableGroup()) {
                equipmentList.forEach { equipment ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = equipment,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .selectable(
                                    selected = selectedEquipment.contains(equipment),
                                    onClick = {
                                        selectedEquipment = if (selectedEquipment.contains(equipment)) {
                                            selectedEquipment - equipment
                                        } else {
                                            selectedEquipment + equipment
                                        }
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para finalizar a configuração
            Button(onClick = {
                // Ação de finalizar, como navegar para uma tela de resumo ou exibir uma confirmação
                navController.navigate("home")
            }) {
                Text("Finalizar Configuração")
            }

            // Exibe os equipamentos selecionados (para visualização)
            Text("Equipamentos selecionados: ${selectedEquipment.joinToString(", ")}")
        }
    }
}

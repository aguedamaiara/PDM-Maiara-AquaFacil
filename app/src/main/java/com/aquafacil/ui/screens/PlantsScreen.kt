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
fun PlantsScreen(navController: NavController) {
    val plantsList = listOf("Java Fern", "Anubias", "Amazon Sword", "Vallisneria", "Cabomba")
    var selectedPlants by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Plantas Aquáticas") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Selecione as plantas do seu aquário:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Lista de plantas
            Column(modifier = Modifier.selectableGroup()) {
                plantsList.forEach { plant ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = plant,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .selectable(
                                    selected = selectedPlants.contains(plant),
                                    onClick = {
                                        selectedPlants = if (selectedPlants.contains(plant)) {
                                            selectedPlants - plant
                                        } else {
                                            selectedPlants + plant
                                        }
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("equipment") }) {
                Text("Próximo")
            }

            // Exibe as plantas selecionadas (para visualização)
            Text("Plantas selecionadas: ${selectedPlants.joinToString(", ")}")
        }
    }
}

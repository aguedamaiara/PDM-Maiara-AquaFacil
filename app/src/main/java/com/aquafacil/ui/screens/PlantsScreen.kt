package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aquafacil.model.AquaticPlant
import com.aquafacil.ui.viewmodel.AquariumViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsScreen(navController: NavController, aquariumViewModel: AquariumViewModel) {
    val plantsList = listOf("Java Fern", "Anubias", "Amazon Sword", "Vallisneria", "Cabomba")
    var selectedPlants by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Selecione as plantas do seu aquário:")
        Spacer(modifier = Modifier.height(16.dp))
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
        Button(onClick = {
            val aquaticPlants = selectedPlants.map { AquaticPlant(id = UUID.randomUUID().toString(), name = it) }
            aquariumViewModel.updatePlants(aquaticPlants)
            navController.navigate("equipment")
        }) {
            Text("Próximo")
        }
    }
}
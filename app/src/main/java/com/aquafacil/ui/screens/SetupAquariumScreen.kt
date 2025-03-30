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
import androidx.navigation.NavHostController
import com.aquafacil.model.*
import com.aquafacil.ui.viewmodel.AquariumViewModel
import java.util.UUID

@Composable
fun SetupAquariumScreen(navController: NavHostController, aquariumViewModel: AquariumViewModel) {
    var step by remember { mutableStateOf(0) }
    val totalSteps = 5

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (step) {
            0 -> StartStep { step++ }
            1 -> TypeStep(aquariumViewModel) { step++ }
            2 -> SizeStep(aquariumViewModel) { step++ }
            3 -> SpeciesStep(aquariumViewModel) { step++ }
            4 -> PlantsStep(aquariumViewModel) { step++ }
            5 -> EquipmentStep(aquariumViewModel) {
                aquariumViewModel.saveAquarium(
                    onSuccess = { /* Navegar para Home */ },
                    onFailure = { println("Erro ao salvar aquário") }
                )
            }
        }
    }
}

@Composable
fun StartStep(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Configuração do Aquário")
        Text("Responda a algumas perguntas para configurar seu aquário.")
        Button(onClick = onNext) { Text("Iniciar Configuração") }
    }
}

@Composable
fun TypeStep(aquariumViewModel: AquariumViewModel, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Seu aquário é de água doce ou salgada?")
        Button(onClick = { aquariumViewModel.setType(AquariumType.FRESHWATER); onNext() }) { Text("Água Doce") }
        Button(onClick = { aquariumViewModel.setType(AquariumType.SALTWATER); onNext() }) { Text("Água Salgada") }
    }
}

@Composable
fun SizeStep(aquariumViewModel: AquariumViewModel, onNext: () -> Unit) {
    var size by remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Qual o tamanho do seu aquário?")
        TextField(value = size, onValueChange = { size = it }, label = { Text("Litros") })
        Button(onClick = { if (size.isNotBlank()) { aquariumViewModel.setSize(size); onNext() } }) { Text("Próximo") }
    }
}

@Composable
fun SpeciesStep(aquariumViewModel: AquariumViewModel, onNext: () -> Unit) {
    val speciesList = listOf("Betta", "Neon Tetra", "Goldfish")
    var selected by remember { mutableStateOf(setOf<String>()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Selecione as espécies:")
        speciesList.forEach { species ->
            Row(modifier = Modifier.selectable(selected.contains(species), onClick = {
                selected = if (selected.contains(species)) selected - species else selected + species
            })) {
                RadioButton(selected.contains(species), onClick = {
                    selected = if (selected.contains(species)) selected - species else selected + species
                })
                Text(species)
            }
        }
        Button(onClick = { aquariumViewModel.updateFishSpecies(selected.map { FishSpecies(UUID.randomUUID().toString(), it) }); onNext() }) {
            Text("Próximo")
        }
    }
}

@Composable
fun PlantsStep(aquariumViewModel: AquariumViewModel, onNext: () -> Unit) {
    val plantsList = listOf("Java Fern", "Anubias")
    var selected by remember { mutableStateOf(setOf<String>()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Selecione as plantas:")
        plantsList.forEach { plant ->
            Row(modifier = Modifier.selectable(selected.contains(plant), onClick = {
                selected = if (selected.contains(plant)) selected - plant else selected + plant
            })) {
                Text(plant)
            }
        }
        Button(onClick = { aquariumViewModel.updatePlants(selected.map { AquaticPlant(UUID.randomUUID().toString(), it) }); onNext() }) {
            Text("Próximo")
        }
    }
}

@Composable
fun EquipmentStep(aquariumViewModel: AquariumViewModel, onFinish: () -> Unit) {
    val equipmentList = listOf("Filtro Canister", "Iluminação LED")
    var selected by remember { mutableStateOf(setOf<String>()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Selecione os equipamentos:")
        equipmentList.forEach { equipment ->
            Row(modifier = Modifier.selectable(selected.contains(equipment), onClick = {
                selected = if (selected.contains(equipment)) selected - equipment else selected + equipment
            })) {
                Text(equipment)
            }
        }
        Button(onClick = { aquariumViewModel.updateEquipment(selected.map { Equipment(UUID.randomUUID().toString(), it, EquipmentType.OTHER) }); onFinish() }) {
            Text("Finalizar Configuração")
        }
    }
}

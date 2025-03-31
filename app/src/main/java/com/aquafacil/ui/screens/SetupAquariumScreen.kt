package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            0 -> StartStep(aquariumViewModel) { step++ }
            1 -> TypeStep(aquariumViewModel) { step++ }
            2 -> SizeStep(aquariumViewModel) { step++ }
            3 -> SpeciesStep(aquariumViewModel) { step++ }
            4 -> PlantsStep(aquariumViewModel) { step++ }
            5 -> EquipmentStep(aquariumViewModel) {
                aquariumViewModel.saveAquarium(
                    onSuccess = {
                        navController.navigate("home") { // Volta para a tela inicial
                            popUpTo("setup") { inclusive = true } // Remove a tela de configuração da pilha
                        }
                    },
                    onFailure = { println("Erro ao salvar aquário") }
                )
            }
        }
    }
}

//@Composable
//fun StartStep(onNext: () -> Unit) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Text("Configuração do Aquário")
//        Text("Responda a algumas perguntas para configurar seu aquário.")
//        Button(onClick = onNext) { Text("Iniciar Configuração") }
//    }
//}

@Composable
fun StartStep(aquariumViewModel: AquariumViewModel, onNext: () -> Unit) {
    var name by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Configuração do Aquário")
        Text("Responda a algumas perguntas para configurar seu aquário.")
        Text("Dê um nome ao seu aquário:")
        TextField(value = name, onValueChange = { name = it }, label = { Text("Nome do Aquário") })
        Button(onClick = { if (name.isNotBlank()) { aquariumViewModel.setName(name); onNext() } }) {
            Text("Próximo")
        }
    }
}

@Composable
fun TypeStep(aquariumViewModel: AquariumViewModel, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Seu aquário é de água doce ou salgada?")
        Button(onClick = { aquariumViewModel.setType(AquariumType.FRESHWATER ); onNext() }) { Text("Água Doce") }
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
    var fishQuantity by remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Quantos peixes tem no seu aquário?")
        val options = listOf("1", "1-5", "5-10", "Mais de 10")
        options.forEach { option ->
            Row(modifier = Modifier.selectable(fishQuantity == option, onClick = {
                fishQuantity = option
            })) {
                RadioButton(fishQuantity == option, onClick = {
                    fishQuantity = option
                })
                Text(option)
            }
        }
        Button(onClick = { aquariumViewModel.setFishQuantity(fishQuantity); onNext() }) {
            Text("Próximo")
        }
    }
}

@Composable
fun PlantsStep(aquariumViewModel: AquariumViewModel, onNext: () -> Unit) {
    var isPlanted by remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Seu aquário é plantado?")
        Row(modifier = Modifier.selectable(isPlanted, onClick = { isPlanted = !isPlanted })) {
            RadioButton(isPlanted, onClick = { isPlanted = true })
            Text("Sim")
        }
        Row(modifier = Modifier.selectable(!isPlanted, onClick = { isPlanted = false })) {
            RadioButton(!isPlanted, onClick = { isPlanted = false })
            Text("Não")
        }
        Button(onClick = { aquariumViewModel.setPlanted(isPlanted); onNext() }) {
            Text("Próximo")
        }
    }
}

@Composable
fun EquipmentStep(aquariumViewModel: AquariumViewModel, onFinish: () -> Unit) {
    var hasEquipment by remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Seu aquário tem equipamentos?")
        Row(modifier = Modifier.selectable(hasEquipment, onClick = { hasEquipment = !hasEquipment })) {
            RadioButton(hasEquipment, onClick = { hasEquipment = true })
            Text("Sim")
        }
        Row(modifier = Modifier.selectable(!hasEquipment, onClick = { hasEquipment = false })) {
            RadioButton(!hasEquipment, onClick = { hasEquipment = false })
            Text("Não")
        }
        Button(onClick = {
            aquariumViewModel.setHasEquipment(hasEquipment)
            onFinish()
        }) {
            Text("Finalizar Configuração")
        }
    }
}
//package com.aquafacil.ui.screens.old
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.selection.selectable
//import androidx.compose.foundation.selection.selectableGroup
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.aquafacil.model.Equipment
//import com.aquafacil.model.EquipmentType
//import com.aquafacil.ui.viewmodel.AquariumViewModel
//import java.util.UUID
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EquipmentScreen(navController: NavController, aquariumViewModel: AquariumViewModel) {
//    val equipmentList = listOf("Filtro Canister", "Filtro Esponja", "Iluminação LED", "Sistema de CO₂", "Aquecedor", "Termômetro")
//    var selectedEquipment by remember { mutableStateOf(setOf<String>()) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("Selecione os equipamentos que utiliza:")
//        Spacer(modifier = Modifier.height(16.dp))
//        Column(modifier = Modifier.selectableGroup()) {
//            equipmentList.forEach { equipment ->
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    Text(
//                        text = equipment,
//                        modifier = Modifier
//                            .padding(start = 8.dp)
//                            .selectable(
//                                selected = selectedEquipment.contains(equipment),
//                                onClick = {
//                                    selectedEquipment = if (selectedEquipment.contains(equipment)) {
//                                        selectedEquipment - equipment
//                                    } else {
//                                        selectedEquipment + equipment
//                                    }
//                                }
//                            )
//                    )
//                }
//            }
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = {
//            val equipment = selectedEquipment.map { Equipment(id = UUID.randomUUID().toString(), name = it, type = EquipmentType.OTHER) }
//            aquariumViewModel.updateEquipment(equipment)
//
//            // Salva o aquário no Firestore
//            aquariumViewModel.saveAquarium(
//                onSuccess = {
//                    navController.navigate("home")
//                },
//                onFailure = { e ->
//                    println("Erro ao salvar aquário: ${e.message}")
//                }
//            )
//        }) {
//            Text("Finalizar Configuração")
//        }
//    }
//}
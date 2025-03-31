package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquafacil.model.Aquarium
import com.aquafacil.model.AquariumType
import com.aquafacil.model.getDisplayName
import com.aquafacil.ui.viewmodel.AquariumViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(aquariumViewModel: AquariumViewModel, onNavigate: () -> Unit) {
    // Obtém o ID do usuário logado
    val auth: FirebaseAuth = Firebase.auth

    // Carrega os aquários do usuário ao iniciar a tela
    LaunchedEffect(Unit) {
        aquariumViewModel.loadAquariums()
    }

    // Observa a lista de aquários
    val aquariums by aquariumViewModel.aquariums.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Tela inicial! Veja seus aquários")
        Spacer(modifier = Modifier.height(16.dp))

        // Exibe a lista de aquários
        if (aquariums.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(aquariums) { aquarium ->
                    AquariumItem(aquarium = aquarium, aquariumViewModel = aquariumViewModel)
                }
            }
        } else {
            Text("Nenhum aquário cadastrado.")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigate) {
            Text("Configurar Aquário")
        }
    }
}

@Composable
fun AquariumItem(aquarium: Aquarium, aquariumViewModel: AquariumViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Nome: ${aquarium.name}")
        Text(
            text = "Tipo de aquário: ${
                when (aquarium.type) {
                    AquariumType.FRESHWATER -> "Água Doce"
                    AquariumType.SALTWATER -> "Água Salgada"
                }
            }"
        )
        Text("Tamanho: ${aquarium.size} Litros")
        Text("Quantidade de peixes: ${aquarium.fishQuantity}")
        Text("Possui Plantas: ${if (aquarium.isPlanted) "Sim" else "Não"}")
        Text("Equipamentos instalados: ${if (aquarium.hasEquipment) "Sim" else "Não"}")
        Button(onClick = { showDialog = true }) { Text("Editar") }
        Spacer(modifier = Modifier.height(8.dp))
        Divider() // Adiciona uma linha divisória entre os aquários
    }
    if (showDialog) {
        EditAquariumDialog(aquarium, aquariumViewModel) { showDialog = false }
    }
}

@Composable
fun EditAquariumDialog(
    aquarium: Aquarium,
    aquariumViewModel: AquariumViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(aquarium.name) }
    var type by remember { mutableStateOf(aquarium.type) }
    var size by remember { mutableStateOf(aquarium.size.toString()) }
    var fishQuantity by remember { mutableStateOf(aquarium.fishQuantity) }
    var isPlanted by remember { mutableStateOf(aquarium.isPlanted) }
    var hasEquipment by remember { mutableStateOf(aquarium.hasEquipment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Aquário") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Aquário") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Tipo de Aquário")
                Row {
                    listOf(AquariumType.FRESHWATER, AquariumType.SALTWATER).forEach {
                        Row(modifier = Modifier.selectable(selected = (type == it), onClick = { type = it })) {
                            RadioButton(selected = (type == it), onClick = { type = it })
                            Text(it.getDisplayName())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = size,
                    onValueChange = { size = it },
                    label = { Text("Tamanho (Litros)") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Quantidade de Peixes")
                listOf("1", "1-5", "5-10", "Mais de 10").forEach { option ->
                    Row(modifier = Modifier.selectable(selected = (fishQuantity == option), onClick = { fishQuantity = option })) {
                        RadioButton(selected = (fishQuantity == option), onClick = { fishQuantity = option })
                        Text(option)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Aquário Plantado")
                Row {
                    RadioButton(selected = isPlanted, onClick = { isPlanted = true })
                    Text("Sim")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = !isPlanted, onClick = { isPlanted = false })
                    Text("Não")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Equipamentos")
                Row {
                    RadioButton(selected = hasEquipment, onClick = { hasEquipment = true })
                    Text("Sim")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = !hasEquipment, onClick = { hasEquipment = false })
                    Text("Não")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                aquariumViewModel.updateAquarium(
                    aquarium.copy(
                        name = name,
                        type = type,
                        size = size.toDoubleOrNull() ?: aquarium.size,
                        fishQuantity = fishQuantity,
                        isPlanted = isPlanted,
                        hasEquipment = hasEquipment
                    )
                )
                onDismiss()
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

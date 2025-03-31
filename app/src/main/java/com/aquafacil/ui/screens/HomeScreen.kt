package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquafacil.model.Aquarium
import com.aquafacil.model.AquariumType
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
                    AquariumItem(aquarium = aquarium)
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
fun AquariumItem(aquarium: Aquarium) {
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
        Spacer(modifier = Modifier.height(8.dp))
        Divider() // Adiciona uma linha divisória entre os aquários
    }
}

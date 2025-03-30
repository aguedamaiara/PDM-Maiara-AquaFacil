package com.aquafacil.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquafacil.R
import com.aquafacil.model.Aquarium
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
        Text("Tipo de aquário: ${aquarium.type}")
        Text("Tamanho: ${aquarium.size} L")
        Text("Espécies: ${aquarium.fishSpecies.joinToString { it.name }}")
        Text("Plantas: ${aquarium.aquaticPlants.joinToString { it.name }}")
        Text("Equipamentos: ${aquarium.equipment.joinToString { it.name }}")
        Spacer(modifier = Modifier.height(8.dp))
        Divider() // Adiciona uma linha divisória entre os aquários
    }
}
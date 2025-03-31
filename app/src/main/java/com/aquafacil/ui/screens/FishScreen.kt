package com.aquafacil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.aquafacil.model.FishSpecies
import com.aquafacil.ui.viewmodel.FishViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FishScreen(navController: NavController) {
    val fishViewModel: FishViewModel = viewModel()
    val fishList by fishViewModel.fishSpecies.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") } // Estado para armazenar a pesquisa

    LaunchedEffect(Unit) {
        fishViewModel.loadFishSpecies()
    }

    // Filtrar os peixes que contêm o texto da pesquisa
    val filteredFishList = fishList.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Espécies de Peixes") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Caixa de pesquisa
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Pesquisar peixe") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            if (filteredFishList.isEmpty()) {
                Text("Nenhuma espécie encontrada.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(filteredFishList) { fish ->
                        FishItem(fish)
                    }
                }
            }
        }
    }
}

@Composable
fun FishItem(fish: FishSpecies) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Carregar a imagem do peixe
            fish.imageUrl?.let {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = "Imagem do peixe ${fish.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Ajuste a altura conforme necessário
                        .padding(bottom = 8.dp)
                )
            }

            Text(text = fish.name, style = MaterialTheme.typography.titleLarge)
            fish.scientificName?.let {
                Text(text = "Nome científico: $it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

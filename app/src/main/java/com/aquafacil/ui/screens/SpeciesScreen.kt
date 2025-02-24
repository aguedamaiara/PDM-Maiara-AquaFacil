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
fun SpeciesScreen(navController: NavController) {
    val speciesList = listOf("Betta", "Neon Tetra", "Goldfish", "Guppy", "Angelfish")
    var selectedSpecies by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Espécies de Peixes") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Selecione as espécies de peixes do seu aquário:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Lista de espécies
            Column(modifier = Modifier.selectableGroup()) {
                speciesList.forEach { species ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = species,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .selectable(
                                    selected = selectedSpecies.contains(species),
                                    onClick = {
                                        selectedSpecies = if (selectedSpecies.contains(species)) {
                                            selectedSpecies - species
                                        } else {
                                            selectedSpecies + species
                                        }
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("plants") }) {
                Text("Próximo")
            }

            // Exibe as espécies selecionadas (para verificação)
            Text("Espécies selecionadas: ${selectedSpecies.joinToString(", ")}")
        }
    }
}

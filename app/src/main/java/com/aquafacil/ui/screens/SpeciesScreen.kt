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
import com.aquafacil.model.FishSpecies
import com.aquafacil.ui.viewmodel.AquariumViewModel
import java.util.UUID
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesScreen(navController: NavController, aquariumViewModel: AquariumViewModel) {
    val speciesList = listOf("Betta", "Neon Tetra", "Goldfish", "Guppy", "Angelfish")
    var selectedSpecies by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Selecione as espécies de peixes do seu aquário:")
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.selectableGroup()) {
            speciesList.forEach { species ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedSpecies.contains(species),
                        onClick = {
                            selectedSpecies = if (selectedSpecies.contains(species)) {
                                selectedSpecies - species
                            } else {
                                selectedSpecies + species
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = species)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val fishSpecies = selectedSpecies.map { FishSpecies(id = UUID.randomUUID().toString(), name = it) }
            aquariumViewModel.updateFishSpecies(fishSpecies)
            navController.navigate("plants")
        }) {
            Text("Próximo")
        }
    }
}
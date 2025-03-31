package com.aquafacil.ui.screens.old

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aquafacil.model.AquariumType
import com.aquafacil.ui.viewmodel.AquariumViewModel

@Composable
fun TypeScreen(navController: NavController, aquariumViewModel: AquariumViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Seu aquário é de água doce ou salgada?")
        Button(onClick = {
            aquariumViewModel.setType(AquariumType.FRESHWATER )
            navController.navigate("size")
        }) {
            Text("Água Doce")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            aquariumViewModel.setType(AquariumType.SALTWATER)
            navController.navigate("size")
        }) {
            Text("Água Salgada")
        }
    }
}
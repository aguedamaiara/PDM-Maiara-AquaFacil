package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aquafacil.model.AquariumType

@Composable
fun TypeScreen(navController: NavController, onTypeSelected: (AquariumType) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            onTypeSelected(AquariumType.FRESHWATER)
            navController.navigate("size")
        }) {
            Text("Água Doce")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onTypeSelected(AquariumType.SALTWATER)
            navController.navigate("size")
        }) {
            Text("Água Salgada")
        }
    }
}

package com.aquafacil.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TypeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Seu aquário é de água doce ou salgada?", modifier = Modifier.padding(bottom = 16.dp))
        Button(onClick = { navController.navigate("size") }) {
            Text("Água Doce")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("size") }) {
            Text("Água Salgada")
        }
    }
}

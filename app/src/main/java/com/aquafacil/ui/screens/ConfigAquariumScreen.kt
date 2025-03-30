package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aquafacil.ui.viewmodel.AquariumViewModel

@Composable
fun ConfigAquariumScreen(navController: NavController) {
    // Na tela principal ou na tela que inicia o fluxo
    val aquariumViewModel: AquariumViewModel = viewModel()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Configuração do Aquário", modifier = Modifier.padding(bottom = 16.dp))
        Text("Responda a estas 5 perguntas sobre seu aquário", modifier = Modifier.padding(bottom = 16.dp))
        Text("Vamos lá!!!", modifier = Modifier.padding(bottom = 16.dp))

        Button(onClick = { navController.navigate("type") }) {
            Text("Iniciar Configuração")
        }
    }
}

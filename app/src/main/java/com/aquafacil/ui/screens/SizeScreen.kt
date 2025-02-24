package com.aquafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizeScreen(navController: NavController) {
    // Variável para armazenar o valor do tamanho do aquário
    var aquariumSize by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tamanho do Aquário") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Qual o tamanho do seu aquário (em litros)?", style = MaterialTheme.typography.titleMedium)

            // Campo de entrada de texto
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = aquariumSize,
                onValueChange = { aquariumSize = it },
                label = { Text("Digite o tamanho (em litros)") },
                modifier = Modifier.fillMaxWidth(0.8f),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão "Próximo"
            Button(
                onClick = {
                    if (aquariumSize.isNotBlank()) {
                        navController.navigate("species")
                    }
                },
                enabled = aquariumSize.isNotBlank() // Só habilita o botão se o campo estiver preenchido
            ) {
                Text("Próximo")
            }
        }
    }
}

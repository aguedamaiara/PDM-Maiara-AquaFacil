package com.aquafacil.ui.screens.old

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aquafacil.ui.viewmodel.AquariumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizeScreen(navController: NavController, aquariumViewModel: AquariumViewModel) {
    var aquariumSize by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Qual o tamanho do seu aquário (em litros)?")
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = aquariumSize,
            onValueChange = { aquariumSize = it },
            label = { Text("Digite o tamanho (em litros)") },
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (aquariumSize.isNotBlank()) {
                    aquariumViewModel.setSize(aquariumSize)
                    navController.navigate("species")
                }
            },
            enabled = aquariumSize.isNotBlank()
        ) {
            Text("Próximo")
        }
    }
}
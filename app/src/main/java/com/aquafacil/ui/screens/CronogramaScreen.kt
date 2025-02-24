package com.aquafacil.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CronogramaScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cronograma de Manutenção") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aqui está o cronograma do seu aquário:",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Exemplo de atividades do cronograma
            val atividades = listOf(
                "Troca parcial de água - Segunda-feira",
                "Limpeza do filtro - Quarta-feira",
                "Verificação do pH - Sexta-feira"
            )

            atividades.forEach { atividade ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = atividade,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

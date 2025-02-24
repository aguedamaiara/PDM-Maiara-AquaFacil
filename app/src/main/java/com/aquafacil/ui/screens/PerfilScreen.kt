package com.aquafacil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquafacil.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Perfil do Usuário") })
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
            // Imagem do usuário (Placeholder)
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de Perfil",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Informações do usuário
            Text(text = "Nome: João da Silva", fontSize = 18.sp)
            Text(text = "E-mail: joao@email.com", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Botão para editar perfil
            Button(onClick = { /* Implementar ação de edição */ }) {
                Text(text = "Editar Perfil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para logout
            Button(
                onClick = { /* Implementar logout */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Sair", color = Color.White)
            }
        }
    }
}

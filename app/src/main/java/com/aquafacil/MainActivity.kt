package com.aquafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aquafacil.ui.theme.AquaFacilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AquaFacilTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo (imagine que o arquivo se chama logo.png)
        Image(
            painter = painterResource(id = R.drawable.logo), // Substitua "logo" com o nome do seu arquivo PNG
            contentDescription = "Logo",
            modifier = Modifier.size(450.dp) // Ajuste o tamanho conforme necessário
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botão "Configurar Aquário"
        Button(onClick = { /* Navegar ou abrir a tela de configuração */ }) {
            Text(text = "Configurar Aquário")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AquaFacilTheme {
        HomeScreen()
    }
}

package com.aquafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.aquafacil.ui.nav.NavGraph
import com.aquafacil.ui.theme.AquaFacilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AquaFacilTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    // Use o innerPadding para evitar sobreposição com a barra de navegação, etc.
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}

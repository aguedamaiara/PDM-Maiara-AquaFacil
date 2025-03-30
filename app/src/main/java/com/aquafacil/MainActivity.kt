package com.aquafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aquafacil.ui.nav.BottomNav
import com.aquafacil.ui.nav.BottomNavBar
import com.aquafacil.ui.nav.NavGraph
import com.aquafacil.ui.theme.AquaFacilTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this) // Inicializa o Firebase
        setContent {
            AquaFacilTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Exibe a BottomNavBar somente se o usuário não estiver na tela de login ou registro
                        if (currentRoute !in listOf("login", "register")) {
                            BottomNavBar(
                                navController = navController,
                                items = listOf(
                                    BottomNav.HomeButton, // "Home" button
                                    BottomNav.CronogramaButton, // "Cronograma" button
                                    BottomNav.PerfilButton // "Perfil" button
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    // Use o innerPadding para evitar sobreposição com a barra de navegação
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // Chama o NavGraph com base no navController
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
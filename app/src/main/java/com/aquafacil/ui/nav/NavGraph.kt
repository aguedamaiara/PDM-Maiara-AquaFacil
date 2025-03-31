package com.aquafacil.ui.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aquafacil.model.Aquarium
import com.aquafacil.ui.screens.old.ConfigAquariumScreen
import com.aquafacil.ui.screens.CronogramaScreen
import com.aquafacil.ui.screens.FishScreen
import com.aquafacil.ui.screens.HomeScreen
import com.aquafacil.ui.screens.LoginScreen
import com.aquafacil.ui.screens.PerfilScreen
import com.aquafacil.ui.screens.RegisterScreen
import com.aquafacil.ui.viewmodel.AquariumViewModel
import com.aquafacil.ui.viewmodel.AquariumViewModelFactory
import com.aquafacil.ui.screens.SetupAquariumScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val aquariumViewModel: AquariumViewModel = viewModel(
        factory = AquariumViewModelFactory(context)
    )
    val aquariums by aquariumViewModel.aquariums.observeAsState(initial = emptyList())

    NavHost(navController = navController, startDestination = "login") {
        // Tela de Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // Tela de Registro
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("home") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // Tela Inicial (Home)
        composable("home") {
            HomeScreen(
                aquariumViewModel = aquariumViewModel,
                onNavigate = { navController.navigate("setup_aquarium") }
            )
        }

        // Tela de Cronograma
        composable("cronograma") {
            CronogramaScreen(aquariumViewModel = aquariumViewModel)
        }

        // Tela de Perfil
        composable("perfil") {
            PerfilScreen(navController)
        }

        // Tela de Configuração do Aquário
        composable("config_aquarium") {
            ConfigAquariumScreen(navController)
        }

        // Tela de Peixes
        composable("fish_screen") {
            FishScreen(navController)
        }

        // Tela de Configuração do Aquário (nova)
        composable("setup_aquarium") {
            SetupAquariumScreen(
                navController = navController,
                aquariumViewModel = aquariumViewModel
            )
        }
    }
}
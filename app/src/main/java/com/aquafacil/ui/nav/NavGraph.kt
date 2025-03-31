package com.aquafacil.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.aquafacil.ui.screens.SetupAquariumScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val aquariumViewModel: AquariumViewModel = viewModel()
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
            val aquariumViewModel: AquariumViewModel = viewModel()
            HomeScreen(
                aquariumViewModel = aquariumViewModel,
                /*onNavigate = { navController.navigate("config_aquarium") }*/
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

        // Adicionar a rota para a tela de peixes
        composable("fish_screen") {
            FishScreen(navController)
        }

        // Substituir as cinco telas por SetupAquariumScreen
        composable("setup_aquarium") {
            val aquariumViewModel: AquariumViewModel = viewModel()
            SetupAquariumScreen(navController = navController, aquariumViewModel = aquariumViewModel)
        }
    }
}
       /* // Tela de Tipo de Aquário
        composable("type") {
            val aquariumViewModel: AquariumViewModel = viewModel()
            TypeScreen(navController = navController, aquariumViewModel = aquariumViewModel)
        }

        // Tela de Tamanho do Aquário
        composable("size") {
            val aquariumViewModel: AquariumViewModel = viewModel()
            SizeScreen(navController = navController, aquariumViewModel = aquariumViewModel)
        }

        // Tela de Espécies de Peixes
        composable("species") {
            val aquariumViewModel: AquariumViewModel = viewModel()
            SpeciesScreen(
                navController = navController,
                aquariumViewModel = aquariumViewModel
            )
        }

        // Tela de Plantas Aquáticas
        composable("plants") {
            val aquariumViewModel: AquariumViewModel = viewModel()
            PlantsScreen(navController = navController, aquariumViewModel = aquariumViewModel)
        }

        // Tela de Equipamentos
        composable("equipment") {
            val aquariumViewModel: AquariumViewModel = viewModel()
            EquipmentScreen(navController = navController, aquariumViewModel = aquariumViewModel)
        }*/

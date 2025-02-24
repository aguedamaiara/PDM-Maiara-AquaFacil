package com.aquafacil.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aquafacil.ui.screens.ConfigAquariumScreen
import com.aquafacil.ui.screens.CronogramaScreen
import com.aquafacil.ui.screens.EquipmentScreen
import com.aquafacil.ui.screens.HomeScreen
import com.aquafacil.ui.screens.LoginScreen
import com.aquafacil.ui.screens.PerfilScreen
import com.aquafacil.ui.screens.PlantsScreen
import com.aquafacil.ui.screens.RegisterScreen
import com.aquafacil.ui.screens.SizeScreen
import com.aquafacil.ui.screens.SpeciesScreen
import com.aquafacil.ui.screens.TypeScreen
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("home") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("home") { HomeScreen(onNavigate = { navController.navigate("config_aquarium") }) }
        composable("cronograma") { CronogramaScreen() }
        composable("perfil") { PerfilScreen() }
        composable("config_aquarium") { ConfigAquariumScreen(navController) }
        composable("type") { TypeScreen(navController) }
        composable("size") { SizeScreen(navController) }
        composable("species") { SpeciesScreen(navController) }
        composable("plants") { PlantsScreen(navController) }
        composable("equipment") { EquipmentScreen(navController) }
    }
}

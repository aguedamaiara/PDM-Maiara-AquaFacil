package com.aquafacil.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aquafacil.ui.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(onNavigate = { navController.navigate("config_aquarium") }) }
        composable("config_aquarium") { ConfigAquariumScreen(navController) }
        composable("type") { TypeScreen(navController) }
        composable("size") { SizeScreen(navController) }
        composable("species") { SpeciesScreen(navController) }
        composable("plants") { PlantsScreen(navController) }
        composable("equipment") { EquipmentScreen(navController) }
    }
}

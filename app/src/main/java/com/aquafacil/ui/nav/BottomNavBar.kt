package com.aquafacil.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aquafacil.ui.screens.*

sealed interface Route {
    @Serializable
    data object Home : Route
    @Serializable
    data object Cronograma : Route
    @Serializable
    data object Perfil : Route
}

sealed class BottomNav(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    data object HomeButton : BottomNav("Início", Icons.Default.Home, "home")
    data object CronogramaButton : BottomNav("Cronograma", Icons.Default.CalendarToday, "cronograma")
    data object PerfilButton : BottomNav("Sua Conta", Icons.Default.Person, "perfil")
}

@Composable
fun BottomNavBar(navController: NavHostController, items: List<BottomNav>) {
    NavigationBar(contentColor = Color.Black) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title, fontSize = 12.sp) },
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) {
                                saveState = true
                            }
                            restoreState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
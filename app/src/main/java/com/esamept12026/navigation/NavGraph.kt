package com.esamept12026.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

import com.esamept12026.view.GameScreen
import com.esamept12026.view.MenuScreen
import com.esamept12026.view.ResultsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {
        composable("menu") {
            MenuScreen(navController)
        }

        composable("game") {
            GameScreen(navController)
        }

        composable("results") {
            ResultsScreen(navController)
        }
    }
}
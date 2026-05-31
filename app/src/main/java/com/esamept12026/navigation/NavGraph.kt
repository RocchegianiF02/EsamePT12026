package com.esamept12026.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

import com.esamept12026.view.GameScreen
import com.esamept12026.view.ResultsScreen

/*
    Grafo di navigazione che identifica le differenti routes.
*/
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "results"
    ) {
        //Schermata di Gioco
        composable("game") {
            GameScreen(navController)
        }

        //Schermata dei Risultati
        composable("results") {
            ResultsScreen(navController)
        }
    }
}
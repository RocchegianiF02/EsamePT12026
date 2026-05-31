package com.esamept12026.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.esamept12026.view.DetailsScreen

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

        //Schermata dei Risultati Partita
        composable("results") {
            ResultsScreen(navController)
        }

        //Schermata dei Dettagli Partita
        composable(
            "details/{gameId}",
            arguments = listOf(navArgument("gameId") {type = NavType.LongType})
        ) {
            backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            DetailsScreen(navController, gameId = gameId)
        }
    }
}
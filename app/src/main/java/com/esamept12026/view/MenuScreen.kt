package com.esamept12026.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.esamept12026.R

/*
    Bottoni all'interno del menù principale che permettono lo spostamento tramite il "NavController" nelle 2 schermate principali dell'applicazione.
*/
@Composable
fun MenuButtons(navController: NavController, modifier: Modifier) {
    Button(
        onClick = {
            navController.navigate("game")
        },
        modifier = modifier
    ) {
        Text(stringResource(R.string.new_game))
    }
    Button(
        onClick = {
            navController.navigate("results")
        },
        modifier = modifier
    ) {
        Text(stringResource(R.string.results))
    }
}

/*
    Componente che implementa il menù principale dell'applicazione, aggiunto per permettere all'utente di spostarsi liberamente fra "Schermata 1" (GameScreen.kt) e "Schermata 2" (ResultsScreen.kt)
    senza essere vincolato necessariamente alla partita.
*/
@Composable
fun MenuScreen(navController: NavController) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = stringResource(R.string.welcome_message),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        MenuButtons(
            navController,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}
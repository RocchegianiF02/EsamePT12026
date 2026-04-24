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
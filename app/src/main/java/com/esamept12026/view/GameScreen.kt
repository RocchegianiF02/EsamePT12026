package com.esamept12026.view

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

import com.esamept12026.R
import com.esamept12026.data.GameState

import com.esamept12026.viewmodel.GameViewModel

//Navigazione Game Over
@Composable
fun GameNavigateToResults(vm : GameViewModel, navController: NavController) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.navigateToResults) {
        if (!state.navigateToResults) return@LaunchedEffect

        vm.resetNavigationEvent()
        navController.navigate("results")
    }
}

//Countdown
@Composable
fun GameCountdown(vm : GameViewModel) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.countdownActive) {
        if (state.countdownActive) {
            vm.startCountdown()
        }
    }
}

//Sequenza
@Composable
fun GameSequence(vm : GameViewModel) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.sequence, state.hasShownSequence, state.countdownActive) {
        vm.playSequence()
    }
}

@Composable
fun GameScreenLandscape(
    vm: GameViewModel = viewModel(),
    navController: NavController
) {
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.weight(1f)) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                ColorGrid(
                    highlighted = state.highlighted,
                    enabled = state.isInputEnabled(),
                    onClick = vm::onUserClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                GameActionButtons(vm,navController)
            }
        }

        GameTextArea(state)
    }
}

@Composable
fun GameScreenPortrait(
    vm: GameViewModel,
    navController: NavController
) {
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize()) {

        ColorGrid(
            highlighted = state.highlighted,
            enabled = state.isInputEnabled(),
            onClick = vm::onUserClick,
            modifier = Modifier.weight(1f)
        )

        GameTextArea(state)

        GameActionButtons(vm,navController)

    }
}

@Composable
fun GameTextArea(state : GameState) {
    Text(
        text = "${stringResource(R.string.score)}: ${state.sequence.size} || ${stringResource(R.string.clears)}: ${state.clears}\n${
            state.userInput.joinToString(
                ", "
            )
        }",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun GameActionButtons(vm: GameViewModel, navController: NavController) {
    Row(Modifier.padding(16.dp)) {
        Button(
            onClick = vm::clear,
            modifier = Modifier.weight(1f)
        ) { Text(stringResource(R.string.clear)) }

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = {
                vm.endGame {
                    navController.navigate("results")
                }
            },
            modifier = Modifier.weight(1f)
        ) { Text(stringResource(R.string.end_game)) }
    }
}

@Composable
fun GameScreenCountdown(vm: GameViewModel) {
    val state by vm.state.collectAsState()
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Text("${state.countdown}", color = Color.White)
    }
}

@Composable
fun GameScreenExitDialog(vm: GameViewModel, navController: NavController) {
    AlertDialog(
        onDismissRequest = vm::closeExitDialog,
        title = { Text(stringResource(R.string.exit_game)) },
        confirmButton = {
            Button(onClick = {
                vm.startNavigation()
                vm.closeExitDialog()
                navController.navigate("menu") {
                    popUpTo("menu") { inclusive = true }
                }
            }) { Text(stringResource(R.string.yes)) }
        },
        dismissButton = {
            Button(onClick = vm::closeExitDialog) {
                Text(stringResource(R.string.no))
            }
        }
    )
}

@Composable
fun GameScreen(
    navController: NavController,
    vm: GameViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    GameNavigateToResults(vm,navController)
    GameCountdown(vm)
    GameSequence(vm)

    BackHandler {
        vm.openExitDialog()
    }

    Box(Modifier.fillMaxSize()) {
        if (isLandscape) {
            //Modalità landscape
            GameScreenLandscape(vm,navController)
        } else {
            //Modalità portrait
            GameScreenPortrait(vm,navController)
        }

        if (state.countdownActive) {
            GameScreenCountdown(vm)
        }

        //Quando l'utente clicca sul rettangolo colorato sbagliato
        if (state.error) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.3f))
            )
        }

        //Alert di uscita dalla partita
        if (state.showExitDialog) {
            GameScreenExitDialog(vm,navController)
        }
    }
}
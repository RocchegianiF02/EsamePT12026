package com.esamept12026.view

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

import com.esamept12026.R
import com.esamept12026.data.GameState
import com.esamept12026.data.SoundManager
import com.esamept12026.repository.GameRepository
import com.esamept12026.repository.GameRepositoryHelper

import com.esamept12026.viewmodel.GameViewModel

//Componente che gestisce l'esecuzione della sequenza randomica che l'utente dovrà ripetere.
@Composable
fun GameSequence(vm : GameViewModel) {
    val state by vm.state.collectAsState()
    if(!state.showing && !state.hasShownSequence) {
        vm.playSequence()
    }
}

//Componente che implementa la schermata di gioco in modalità "landscape".
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
                GameActionButtons(state,vm,navController)
            }
        }

        GameTextArea(state)
    }
}

//Componente che implementa la schermata di gioco in modalità "portrait".
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

        GameActionButtons(state,vm,navController)

    }
}

//Componente che implementa l'area di testo che mostra la sequenza inserita e il numero di "reset" della sequenza inserita dall'utente all'interno della schermata di gioco.
@Composable
fun GameTextArea(state : GameState) {
    Text(
        text = "${stringResource(R.string.score)} : ${state.sequence.size}\n${
            state.userInput.joinToString(
                ", "
            )
        }",
        modifier = Modifier.padding(16.dp)
    )
}

//Componente che implementa i bottoni inseriti all'interno della "Schermata 1".
@Composable
fun GameActionButtons(state : GameState, vm: GameViewModel, navController: NavController) {
    Row(Modifier.padding(16.dp)) {
        Button(
            onClick = {
                vm.startGame()
            },
            modifier = Modifier.weight(1f),
            enabled = !state.gameStarted && !state.locked && !state.navigating
        ) { Text(stringResource(R.string.start_game)) }

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = {
                if(state.gamePaused)
                    vm.resumeGame()
                else
                    vm.pauseGame()
            },
            modifier = Modifier.weight(1f),
            enabled = state.gameStarted && !state.locked && !state.navigating
        ) {
            if(!state.gamePaused)
                Text(stringResource(R.string.pause_game))
            else
                Text(stringResource(R.string.resume_game))
        }

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = {
                vm.onBackPressed(
                    navigateResults = {
                        navController.popBackStack()
                    }
                )
            },
            modifier = Modifier.weight(1f),
            enabled = state.gameStarted && !state.locked && !state.navigating
        ) { Text(stringResource(R.string.end_game)) }
    }
}



//Componente che implementa l'intera "Schermata 1".
@Composable
fun GameScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context.applicationContext) }
    val activity = context as ComponentActivity

    val dbHelper = remember { GameRepositoryHelper(context.applicationContext) }
    val repository = remember { GameRepository(dbHelper) }

    val vm: GameViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(repository, soundManager) as T
            }
        }
    )

    val state by vm.state.collectAsState()

    //Reset dello stato
    LaunchedEffect(Unit) {
        if (!state.gameStarted) {
            vm.resetForNewGame()
        }
    }

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    GameSequence(vm)

    BackHandler {

        vm.onBackPressed(
            navigateResults = {
                navController.popBackStack()
            }
        )

    }

    Box(Modifier.fillMaxSize()) {
        if (isLandscape) {
            //Modalità landscape.
            GameScreenLandscape(vm,navController)
        } else {
            //Modalità portrait.
            GameScreenPortrait(vm,navController)
        }

        //Quando l'utente clicca sul rettangolo colorato sbagliato viene mostrata una schermata rossa a segnalare l'errore.
        if (state.error) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.3f))
            )
            vm.resetError()
        }
    }
}
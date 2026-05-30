package com.esamept12026.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.esamept12026.R

import com.esamept12026.data.GameRepository
import com.esamept12026.model.GameResult

//Componente che implementa la "header" della "tabella" contenente la lista delle partite giocate nella sessione corrente e i relativi risultati.
@Composable
fun ResultsHeader(modifier: Modifier) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.squares_pressed),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(R.string.sequence_pressed),
            modifier = Modifier.weight(2f)
        )
        /*
        Text(
            text = stringResource(R.string.clears),
            modifier = Modifier.weight(1f)
        )
        */
    }
}

//Componente che implementa il "corpo" della "tabella" contenente la lista delle partite giocate nella sessione corrente e i relativi risultati.
@Composable
fun ResultsBody(games : SnapshotStateList<GameResult>, modifier : Modifier) {
    LazyColumn( modifier = modifier ) {
        items(games) { g ->
            val sequenceText = g.sequence.joinToString(", ")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                //Colonna 1 - numero elementi
                Text(
                    text = "${g.sequence.size}",
                    modifier = Modifier.weight(1f)
                )

                if(sequenceText.isEmpty()){
                    //Colonna 2 - fallimento al primo pulsante
                    Text(
                        text = stringResource(R.string.on_first_fail),
                        modifier = Modifier.weight(2f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                else{
                    //Colonna 2 - sequenza pulsanti premuti
                    Text(
                        text = sequenceText,
                        modifier = Modifier.weight(2f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                /*
                //Colonna 3 - reset dei pulsanti premuti eseguiti
                Text(
                    text = "${g.clears}",
                    modifier = Modifier.weight(1f)
                )
                */
            }
        }
    }
}

//Componente che implementa i bottoni della "Schermata 2" e le relative funzionalità.
@Composable
fun ResultsButtons(
    //onClickShowDialog : (Boolean) -> Unit,
    navController : NavController,
    modifier: Modifier
) {
    Button(
        //onClick = { onClickShowDialog(true) },
        onClick = { navController.navigate("game") },
        modifier = modifier
    ) {
        Text(stringResource(R.string.new_game))
    }

    /*
    Button(
        onClick = {
            navController.navigate("menu") {
                popUpTo("menu") { inclusive = true }
            }
        },
        modifier = modifier
    ) {
        Text(stringResource(R.string.menu))
    }
    */
}

//Componente che implementa il riquadro che indica al giocatore che non sono ancora state avviate partite nella sessione corrente.
@Composable
fun ResultsNoGamesBox(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.not_started_yet),
            textAlign = TextAlign.Center
        )
    }
}

/*
//Componente che implementa la finestra di dialogo che permette di iniziare una nuova partita.
@Composable
fun ResultsDialog(onClickShowDialog : (Boolean) -> Unit, navController: NavController) {
    AlertDialog(
        onDismissRequest = { onClickShowDialog(false) },
        title = { Text(stringResource(R.string.start_new_game)) },
        confirmButton = {
            Button(onClick = {
                onClickShowDialog(false)
                navController.navigate("game") {
                    popUpTo("game") { inclusive = true }
                }
            }) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            Button(onClick = { onClickShowDialog(false) }) {
                Text(stringResource(R.string.no))
            }
        }
    )
}
*/

//Componente che implementa l'intera "Schermata 2".
@Composable
fun ResultsScreen(navController: NavController) {

    val games = GameRepository.games
    //var showDialog by remember { mutableStateOf(false) }

    /*
    //Permette di invocare la finestra di dialogo quando "premo" il tasto "indietro" del dispositivo.
    BackHandler {
        showDialog = true
    }
    */

    if(!games.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ResultsHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Column {
                ResultsBody(games, modifier = Modifier.weight(1f))

                ResultsButtons(
                    //onClickShowDialog = { newValue -> showDialog = newValue },
                    navController,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
    }
    else{
        Column(modifier = Modifier.fillMaxSize()) {
            ResultsNoGamesBox(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            )

            ResultsButtons(
                //onClickShowDialog = { newValue -> showDialog = newValue },
                navController,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }

    /*
    //Finestra di dialogo che ci chiede la conferma di iniziare una nuova partita.
    if (showDialog) {
        ResultsDialog(
            onClickShowDialog = { newValue -> showDialog = newValue },
            navController
        )
    }
    */
}
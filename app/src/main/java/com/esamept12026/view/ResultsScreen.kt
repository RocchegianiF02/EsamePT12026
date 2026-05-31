package com.esamept12026.view

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.lifecycle.ViewModelProvider

import com.esamept12026.R
import com.esamept12026.data.SoundManager

//import com.esamept12026.data.GameRepository
import com.esamept12026.model.GameRepository
import com.esamept12026.model.GameRepositoryHelper
import com.esamept12026.model.GameResult
import com.esamept12026.viewmodel.GameViewModel


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
fun ResultsBody(navController: NavController,
                //games : SnapshotStateList<GameResult>,
                games : List<GameResult>,
                modifier : Modifier
) {
    LazyColumn( modifier = modifier ) {
        items(games) { g ->
            //val sequenceText = g.sequence.joinToString(", ")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{
                        navController.navigate("details/${g.id}")
                    }
                    .padding(12.dp)
            ) {
                //Colonna 1 - numero elementi
                Text(
                    text = "${g.errorIndex}",
                    modifier = Modifier.weight(1f)
                )
                /*
                if(sequenceText.isEmpty()){
                    //Colonna 2 - fallimento al primo pulsante
                    Text(
                        text = stringResource(R.string.on_first_fail),
                        modifier = Modifier.weight(2f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                else{  }
                */
                val sequenceText = buildColoredSequence(g.sequence,g.errorIndex)
                //Colonna 2 - sequenza pulsanti premuti
                Text(
                    text = sequenceText,
                    modifier = Modifier.weight(2f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

//Da spostare in "GameViewModel" e da richiamare tramite "vm.buildColoredSequence"
fun buildColoredSequence(
    sequence: List<String>,
    errorIndex: Int
): AnnotatedString {

    return buildAnnotatedString {

        sequence.forEachIndexed { index, value ->

            withStyle(
                style = SpanStyle(
                    color =
                        if (index < errorIndex)
                            Color.Green
                        else
                            Color.Red
                )
            ) {
                append(value)
            }

            if (index < sequence.lastIndex) {
                append(", ")
            }
        }
    }
}

//Componente che implementa i bottoni della "Schermata 2" e le relative funzionalità.
@Composable
fun ResultsButtons(
    navController : NavController,
    modifier: Modifier
) {
    Button(
        onClick = { navController.navigate("game") },
        modifier = modifier
    ) {
        Text(stringResource(R.string.new_game))
    }
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

//Componente che implementa l'intera "Schermata 2".
@Composable
fun ResultsScreen(navController: NavController) {

    /*
    val context = LocalContext.current

    val dbHelper = remember { GameRepositoryHelper(context.applicationContext) }
    val repository = remember { GameRepository(dbHelper) }

    //Log.d("INFORMAZIONI?","I AM STILL ALIVE!")

    val viewModel: GameViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(repository) as T
            }
        }
    )
    */

    val context = LocalContext.current
    val soundManager = remember { SoundManager(context.applicationContext) }
    val activity = context as ComponentActivity

    val dbHelper = remember { GameRepositoryHelper(context.applicationContext) }
    val repository = remember { GameRepository(dbHelper) }

    val viewModel: GameViewModel = viewModel(
        viewModelStoreOwner = activity,            // ← stessa istanza dell'Activity
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(repository,soundManager) as T
            }
        }
    )

    //Log.d("INFORMAZIONI?","I AM STILL ALIVE PT2!")

    val games : List<GameResult> by viewModel.results.collectAsState()

    if (games.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ResultsHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            ResultsBody(
                navController = navController,
                games = games,
                modifier = Modifier.weight(1f)
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            ResultsButtons(
                navController = navController,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            ResultsNoGamesBox(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            ResultsButtons(
                navController = navController,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }

    /*
    val games = GameRepository.games

    if(!games.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ResultsHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Column {
                ResultsBody(navController, games, modifier = Modifier.weight(1f))

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
    */

}
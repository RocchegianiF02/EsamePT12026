package com.esamept12026.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import com.esamept12026.data.SoundManager
import com.esamept12026.repository.GameRepository
import com.esamept12026.repository.GameRepositoryHelper
import com.esamept12026.data.GameResult
import com.esamept12026.viewmodel.GameViewModel

import com.esamept12026.view.components.GameResultsHeader


@Composable
fun DetailsBody(result: GameResult, viewModel: GameViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        //Colonna 1 - numero elementi
        Text(
            //text = "${g.errorIndex}",
            text = "${result.errorIndex}",
            modifier = Modifier.weight(1f)
        )
        //Colonna 2 - sequenza pulsanti premuti
        val sequenceText = viewModel.buildColoredSequence(result.sequence,result.errorIndex)
        Text(
            text = sequenceText,
            modifier = Modifier.weight(2f),
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DetailsScreen(gameId: Long){
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context.applicationContext) }
    val activity = context as ComponentActivity

    val dbHelper = remember { GameRepositoryHelper(context.applicationContext) }
    val repository = remember { GameRepository(dbHelper) }

    val viewModel: GameViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(repository,soundManager) as T
            }
        }
    )

    var result by remember { mutableStateOf<GameResult?>(null) }

    LaunchedEffect(gameId) {
        result = viewModel.getById(gameId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GameResultsHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        if (result != null) {
            DetailsBody(
                result = result!!,
                viewModel
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
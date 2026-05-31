package com.esamept12026.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.esamept12026.R

/*
class DetailsScreen {
}
*/

//Uguale a "ResultsHeader" in "ResultsScreen" <-- da rendere componenti assestanti
@Composable
fun DetailsHeader(modifier: Modifier) {
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
    }
}

//Simile a "ResultsBody" in "ResultsScreen"
@Composable
fun DetailsBody(navController: NavController, gameId : Long, modifier : Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        //Colonna 1 - numero elementi
        Text(
            //text = "${g.errorIndex}",
            text = "${gameId}",
            modifier = Modifier.weight(1f)
        )
        //val sequenceText = buildColoredSequence(g.sequence,g.errorIndex)
        //Colonna 2 - sequenza pulsanti premuti
        Text(
            //text = sequenceText,
            text = "${gameId}",
            modifier = Modifier.weight(2f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DetailsScreen(navController: NavController, gameId: Long){
    Column(modifier = Modifier.fillMaxSize()) {
        DetailsHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        DetailsBody(
            navController,
            gameId,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
    }
}
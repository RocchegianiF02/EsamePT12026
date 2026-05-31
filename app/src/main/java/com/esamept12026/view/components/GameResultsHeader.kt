package com.esamept12026.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

import com.esamept12026.R

@Composable
fun GameResultsHeader(modifier: Modifier) {
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
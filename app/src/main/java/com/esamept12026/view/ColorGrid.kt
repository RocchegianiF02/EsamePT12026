package com.esamept12026.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.esamept12026.data.GameColors

const val ROW_LENGTH = 3
const val COL_LENGTH = 2

@Composable
fun ColorGrid(
    highlighted: String?,
    enabled: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GameColors.colors

    Column(modifier.fillMaxSize()) {
        for (i in 0 until ROW_LENGTH) {
            Row(Modifier.weight(1f)) {
                for (j in 0 until COL_LENGTH) {
                    if(i * COL_LENGTH + j < colors.size) {
                        val item = colors[i * COL_LENGTH + j]
                        val active = item.code == highlighted

                        val color by animateColorAsState(
                            if (active) item.color.copy(alpha = 0.4f)
                            else item.color,
                            label = ""
                        )

                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .background(color)
                                .clickable(enabled = enabled) {
                                    onClick(item.code)
                                }
                        )
                    }
                }
            }
        }
    }
}
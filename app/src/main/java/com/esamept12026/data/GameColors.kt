package com.esamept12026.data

import androidx.compose.ui.graphics.Color

/*
     Oggetto contenente la lista con le associazioni codice - colore
*/

const val RED_BUTTON = "R"
const val GREEN_BUTTON = "G"
const val BLUE_BUTTON = "B"
const val YELLOW_BUTTON = "Y"
const val MAGENTA_BUTTON = "M"
const val CYAN_BUTTON = "C"

object GameColors {
    val colors = listOf(
        GameColor(RED_BUTTON, Color.Red),
        GameColor(GREEN_BUTTON, Color.Green),
        GameColor(BLUE_BUTTON, Color.Blue),
        GameColor(YELLOW_BUTTON, Color.Yellow),
        GameColor(MAGENTA_BUTTON, Color.Magenta),
        GameColor(CYAN_BUTTON, Color.Cyan)
    )
}
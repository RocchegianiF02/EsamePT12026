package com.esamept12026.data

/*
    Classe contenente gli stati utilizzati all'interno dell'applicazione e alcuni campi condivisi come ad esempio la sequenza di input inseriti
    da parte dell'utente durante la partita (userInput).
*/
data class GameState(
    val sequence: List<String> = emptyList(),
    val userInput: List<String> = emptyList(),
    val userIndex: Int = 0,
    val clears: Int = 0,

    val locked: Boolean = false,
    val showing: Boolean = false,
    val error: Boolean = false,

    val showExitDialog: Boolean = false,

    val countdown: Int = 4,
    val isCountingDown: Boolean = false,
    val countdownActive: Boolean = true,
    val hasShownSequence: Boolean = false,

    val navigating: Boolean = false,
    val navigateToResults: Boolean = false,

    val highlighted: String? = null
){
    fun isInputEnabled(): Boolean =
        !locked && !showing && !countdownActive && !navigating
}
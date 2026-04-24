package com.esamept12026.model

/*
    Classe dati che rappresenta la sequenza e i reset premuti dall'utente durante la sessione corrente.
*/
data class GameResult(
    val sequence: List<String>,
    val clears: Int
)
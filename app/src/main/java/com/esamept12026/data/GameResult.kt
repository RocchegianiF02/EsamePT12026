package com.esamept12026.data

/*
 *  Classe dati che rappresenta i dati della partita giocata dall'utente.
 *  Vengono salvati la sequenza di gioco e l'indice dove l'utente ha commesso
 *  un errore durante la sequenza fornita.
 */
data class GameResult(
    val id: Long = 0,
    val sequence: List<String>,
    val errorIndex: Int
)
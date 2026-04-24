package com.esamept12026.data

import androidx.compose.runtime.mutableStateListOf

import com.esamept12026.model.GameResult

/*
    Oggetto che rappresenta la "repository" dei risultati delle partite giocate durante la sessione corrente.
*/
object GameRepository {
    val games = mutableStateListOf<GameResult>()
}
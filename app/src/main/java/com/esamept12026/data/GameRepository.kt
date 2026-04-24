package com.esamept12026.data

import androidx.compose.runtime.mutableStateListOf

import com.esamept12026.model.GameResult

object GameRepository {
    val games = mutableStateListOf<GameResult>()
}
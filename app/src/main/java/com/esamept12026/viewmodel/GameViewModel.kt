package com.esamept12026.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esamept12026.data.GameColors

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.esamept12026.data.GameRepository
import com.esamept12026.data.GameState
import com.esamept12026.model.GameResult

//Questa classe sarà responsabile della preparazione e la gestione dei dati dentro ai componenti dell'applicazione (in quanto ViewModel).
class GameViewModel : ViewModel() {

    val state = MutableStateFlow(GameState())
    private val colors = GameColors.colors

    fun startGame() {
        state.value = GameState(
            sequence = listOf(colors.random().code),
            gameStarted = true
        )
        //state.update { it.copy(gameStarted = true) }
    }

    fun pauseGame() {
        state.update { it.copy(gamePaused = true) }
    }

    fun resumeGame() {
        state.update { it.copy(gamePaused = false) }
    }

    fun playSequence() {
        viewModelScope.launch {

            val current = state.value

            if (current.sequence.isEmpty() || current.hasShownSequence) return@launch

            state.update { it.copy(showing = true) }

            for (c in current.sequence) {

                while (state.value.gamePaused) {
                    delay(100)
                }

                state.update { it.copy(highlighted = c) }
                delay(400)

                state.update { it.copy(highlighted = null) }
                delay(120)
            }

            state.update {
                it.copy(
                    showing = false,
                    hasShownSequence = true
                )
            }
        }
    }

    fun onUserClick(color: String) {
        //val s = state.value

        //Previene click accidentali da parte dell'utente durante le fasi critiche all'interno della nostra applicazione.
        //if (s.locked || s.showing || s.countdownActive || s.navigating) return
        if (state.value.locked || state.value.showing || state.value.navigating || !state.value.gameStarted || state.value.gamePaused) return

        //Previene l'assegnazione del valore che dovrebbe essere confrontato in caso di valori NULL.
        val expected = state.value.sequence.getOrNull(state.value.userIndex) ?: return

        if(!state.value.hasStartedMatch)
            state.update { it.copy(hasStartedMatch = true) }

        if (color == expected) {
            val newIndex = state.value.userIndex + 1

            if (newIndex == state.value.sequence.size) {
                state.update {
                    it.copy(
                        sequence = it.sequence + colors.random().code,
                        userInput = emptyList(),
                        userIndex = 0,
                        hasShownSequence = false
                    )
                }
            } else {
                state.update {
                    it.copy(
                        userInput = it.userInput + color,
                        userIndex = newIndex
                    )
                }
            }
        } else {
            gameOver()
        }
    }

    private fun gameOver() {
        state.update {
            it.copy(
                locked = true,
                error = true,
                navigating = true,
                navigateToResults = true,
                gameStarted = false
            )
        }
        viewModelScope.launch {
            delay(300)
            //GameRepository.games.add(GameResult(state.value.sequence,state.value.userIndex))
            saveCurrentGame()
        }
    }

    /*
    fun endGame(
        onNavigate: () -> Unit
    ) {
        val s = state.value

        if (s.locked || s.showing || s.navigating) return

        state.update {
            it.copy(
                navigating = true,
                gameStarted = false,
                gameOver = true
            )
        }

        saveCurrentGame()
        onNavigate()
    }
    */

    fun onBackPressed(
        navigateResults: () -> Unit
    ) {
        val s = state.value

        if (!s.gameStarted || !s.hasStartedMatch) {
            navigateResults()
            return
        }

        Log.i("INFORMAZIONI APP","LA PARTITA CONCLUSA ERA IN CORSO!")

        finishGame()
        navigateResults()
    }

    private fun finishGame() {
        state.update {
            it.copy(
                locked = true,
                gameOver = true,
                gameStarted = false
            )
        }
        saveCurrentGame()
    }

    private fun saveCurrentGame() {
        val s = state.value

        GameRepository.games.add(
            GameResult(
                sequence = s.sequence,
                errorIndex = s.userIndex
            )
        )
    }

}
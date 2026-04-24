package com.esamept12026.viewmodel

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

//Questa classe sarà responsabile della preparazione e la gestione dei dati dentro ai component (in quanto ViewModel)
class GameViewModel : ViewModel() {

    val state = MutableStateFlow(GameState())
    val colors = GameColors.colors

    init {
        startGame(withCountdown = true)
    }

    fun startGame(withCountdown: Boolean = true) {
        state.value = GameState(
            sequence = listOf(colors.random().code),
            countdown = 4,
            countdownActive = withCountdown
        )
    }

    fun startCountdown() {
        if (state.value.isCountingDown) return

        viewModelScope.launch {
            state.update { it.copy(isCountingDown = true) }
            while (state.value.countdown > 0) {
                delay(1000)
                state.update { it.copy(countdown = it.countdown - 1) }
            }
            state.update { it.copy(
                countdownActive = false,
                isCountingDown = false
            )}
        }
    }

    fun markSequenceShown() {
        state.update { it.copy(hasShownSequence = true) }
    }

    fun setShowing(v: Boolean) {
        state.update { it.copy(showing = v) }
    }

    fun startNavigation() {
        state.update { it.copy(navigating = true) }
    }

    fun resetNavigationEvent() {
        state.update { it.copy(navigateToResults = false) }
    }

    fun openExitDialog() {
        state.update { it.copy(showExitDialog = true) }
    }

    fun closeExitDialog() {
        state.update { it.copy(showExitDialog = false) }
    }

    fun playSequence() {
        viewModelScope.launch {
            val current = state.value

            if (current.sequence.isEmpty() ||
                current.hasShownSequence ||
                current.countdownActive
            ) return@launch

            state.update { it.copy(showing = true) }

            for (c in current.sequence) {
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
        val s = state.value

        //Previene click accidentali da parte dell'utente durante le fasi critiche all'interno della nostra applicazione
        if (s.locked || s.showing || s.countdownActive || s.navigating) return

        //Previene l'assegnazione del valore che dovrebbe essere confrontato in caso di valori NULL
        val expected = s.sequence.getOrNull(s.userIndex) ?: return

        if (color == expected) {
            val newIndex = s.userIndex + 1

            if (newIndex == s.sequence.size) {
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
                navigateToResults = true
            )
        }
        viewModelScope.launch {
            delay(300)

            val s = state.value
            GameRepository.games.add(GameResult(s.userInput, s.clears))

            //Impedisce il countdown dopo un "Game Over"
            startGame(withCountdown = false)
        }
    }

    fun clear() {
        val s = state.value

        //Previene errori di reset non voluti nelle sezioni critiche del codice
        if (s.locked || s.showing || s.countdownActive || s.navigating) return

        state.update {
            it.copy(
                userInput = emptyList(),
                userIndex = 0,
                clears = it.clears + 1
            )
        }
    }

    fun endGame(onNavigate: () -> Unit) {
        val s = state.value

        //Previene errori di reset non voluti nelle sezioni critiche del codice
        if (s.locked || s.showing || s.countdownActive || s.navigating) return

        state.update { it.copy(navigating = true) }

        GameRepository.games.add(GameResult(s.userInput, s.clears))

        startGame(withCountdown = false)
        onNavigate()
    }

}
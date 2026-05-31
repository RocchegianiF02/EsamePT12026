package com.esamept12026.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esamept12026.data.GameColors

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.esamept12026.data.GameState
import com.esamept12026.data.SoundManager
import com.esamept12026.repository.GameRepository
import com.esamept12026.data.GameResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.StateFlow

//Questa classe sarà responsabile della preparazione e la gestione dei dati dentro ai componenti dell'applicazione (in quanto ViewModel).
class GameViewModel(
    private val repository: GameRepository,
    private val soundManager: SoundManager
) : ViewModel() {

    val state = MutableStateFlow(GameState())
    private val colors = GameColors.colors

    private val _results = MutableStateFlow<List<GameResult>>(emptyList())
    val results: StateFlow<List<GameResult>> = _results

    private var playJob: Job? = null

    init {
        loadResults()
    }

    fun loadResults() {
        viewModelScope.launch {
            _results.value = repository.getAll()
        }
    }

    fun add(sequence: List<String>, errorIndex: Int) {
        viewModelScope.launch {
            repository.inserisci(GameResult(sequence = sequence, errorIndex = errorIndex))
            loadResults()
        }
    }

    suspend fun getById(gameId: Long): GameResult? {
        return repository.getById(gameId)
    }

    fun startGame() {
        if(state.value.gameStarted) return
        playJob?.cancel()
        state.value = GameState(
            sequence = listOf(colors.random().code),
            gameStarted = true
        )
    }

    fun resetForNewGame() {
        playJob?.cancel()
        state.value = GameState()
    }

    fun pauseGame() {
        state.update { it.copy(gamePaused = true) }
    }

    fun resumeGame() {
        state.update { it.copy(gamePaused = false) }
    }

    fun resetError() {
        viewModelScope.launch {
            delay(500)
            state.update { it.copy(
                error = false,
                locked = false
            ) }
        }
        Log.d("DEBUG","STATO // ERROR:  "+state.value.error+" // GAME STARTED: "+state.value.gameStarted+" // GAME PAUSE: "+state.value.gamePaused+" // NAVIGATING: "+state.value.navigating)
    }

    fun playSequence() {
        playJob?.cancel()
        playJob = viewModelScope.launch {
            if (state.value.sequence.isEmpty() || state.value.hasShownSequence) return@launch

            state.update { it.copy(showing = true) }

            try {
                for (c in state.value.sequence) {
                    if(!state.value.gameStarted)
                        break
                    // Attendi se in pausa
                    while (state.value.gamePaused) {
                        delay(100)
                    }
                    state.update { it.copy(highlighted = c) }
                    soundManager.play(c)
                    delay(400)
                    state.update { it.copy(highlighted = null) }
                    delay(120)
                }
            } finally {
                state.update {
                    it.copy(
                        showing = false,
                        hasShownSequence = true,
                        highlighted = null
                    )
                }
            }
        }
    }

    fun onUserClick(color: String) {
        //Previene click accidentali da parte dell'utente durante le fasi critiche all'interno della nostra applicazione.
        if (state.value.locked || state.value.showing || state.value.navigating || !state.value.gameStarted || state.value.gamePaused) return

        soundManager.play(color)

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
                gameStarted = false
            )
        }
    }

    fun onBackPressed(
        navigateResults: () -> Unit
    ) {
        if (!state.value.hasStartedMatch) {
            navigateResults()
            finishGameNoSave()
            return
        }

        navigateResults()
        finishGameSave()
    }

    private fun finishGameNoSave() {
        state.update {
            it.copy(
                locked = true,
                gameOver = true,
                gameStarted = false
                ,navigating = false
            )
        }
    }

    private fun finishGameSave() {
        state.update {
            it.copy(
                locked = true,
                gameOver = true,
                gameStarted = false
                ,navigating = false
            )
        }
        saveCurrentGame()
    }

    private fun saveCurrentGame() {
        viewModelScope.launch(NonCancellable) {
            val s = state.value
            add(s.sequence,s.userIndex)
        }
    }

    fun buildColoredSequence(
        sequence: List<String>,
        errorIndex: Int
    ): AnnotatedString {

        return buildAnnotatedString {

            sequence.forEachIndexed { index, value ->

                withStyle(
                    style = SpanStyle(
                        color =
                            if (index < errorIndex)
                                Color.Green
                            else
                                Color.Red
                    )
                ) {
                    append(value)
                }

                if (index < sequence.lastIndex) {
                    append(", ")
                }
            }
        }
    }

}
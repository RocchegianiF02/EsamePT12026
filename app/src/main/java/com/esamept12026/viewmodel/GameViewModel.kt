package com.esamept12026.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esamept12026.data.GameColors

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.esamept12026.data.GameState
import com.esamept12026.model.GameResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.StateFlow

//Questa classe sarà responsabile della preparazione e la gestione dei dati dentro ai componenti dell'applicazione (in quanto ViewModel).
class GameViewModel(private val repository: com.esamept12026.model.GameRepository) : ViewModel() {

    val state = MutableStateFlow(GameState())
    private val colors = GameColors.colors

    private val _results = MutableStateFlow<List<GameResult>>(emptyList())
    val results: StateFlow<List<GameResult>> = _results

    private var playJob: Job? = null

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //REPOSITORY FUNCTIONS - FIRST TEST

    init {
        //Log.d("DATABASE SERVICE","CARICO I RISULTATI ESISTENTI ...")
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

    /*
    fun getById(gameId: Long) : GameResult? {
        var gr : GameResult? = null
        viewModelScope.launch {
            gr = repository.getById(gameId)
        }
        return gr
    }
    */

    suspend fun getById(gameId: Long): GameResult? {
        return repository.getById(gameId)
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            repository.elimina(id)
            loadResults()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun startGame() {
        if(state.value.gameStarted) return
        playJob?.cancel()
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

    fun resetError() {
        viewModelScope.launch {
            delay(500)
            state.update { it.copy( error = false ) }
        }
    }

    fun playSequence() {
        /*
        viewModelScope.launch {

            //val current = state.value

            if (state.value.sequence.isEmpty() || state.value.hasShownSequence) return@launch

            state.update { it.copy(showing = true) }

            for (c in state.value.sequence) {

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
        */
        playJob?.cancel()
        playJob = viewModelScope.launch {
            if (state.value.sequence.isEmpty() || state.value.hasShownSequence) return@launch

            state.update { it.copy(showing = true) }

            try {
                for (c in state.value.sequence) {
                    // Attendi se in pausa
                    while (state.value.gamePaused) {
                        delay(100)
                    }
                    state.update { it.copy(highlighted = c) }
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
        /*
        viewModelScope.launch {
            delay(300)
            //GameRepository.games.add(GameResult(state.value.sequence,state.value.userIndex))
            saveCurrentGame()
        }
        */
    }

    fun onBackPressed(
        navigateResults: () -> Unit
    ) {
        //val s = state.value

        //if (!state.value.gameStarted || !state.value.hasStartedMatch) {
        if (!state.value.hasStartedMatch) {
            finishGameNoSave()
            navigateResults()
            return
        }

        //Log.i("INFORMAZIONI APP","LA PARTITA CONCLUSA ERA IN CORSO!")
        finishGameSave()
        navigateResults()
    }

    private fun finishGameNoSave() {
        state.update {
            it.copy(
                locked = true,
                gameOver = true,
                gameStarted = false
            )
        }
    }

    private fun finishGameSave() {
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
        viewModelScope.launch(NonCancellable) {
            val s = state.value
            add(s.sequence,s.userIndex)
        }
        /*
        GameRepository.games.add(
            GameResult(
                sequence = s.sequence,
                errorIndex = s.userIndex
            )
        )
        */
    }

}
package com.glowberrylabs.fruitsnake.game

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glowberrylabs.fruitsnake.audio.GameSounds
import com.glowberrylabs.fruitsnake.data.ScoreStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val scoreStore: ScoreStore,
    private val sounds: GameSounds,
    private val engine: SnakeEngine = SnakeEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GameUiState(
            game = engine.newGame(),
            highScore = scoreStore.loadHighScore(),
            soundEnabled = scoreStore.isSoundEnabled()
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var tickJob: Job? = null

    fun play() {
        when (_uiState.value.game.status) {
            GameStatus.READY, GameStatus.GAME_OVER, GameStatus.WON -> startNewGame()
            GameStatus.PAUSED -> resume()
            GameStatus.RUNNING -> Unit
        }
    }

    fun restart() {
        startNewGame()
    }

    fun pause() {
        val current = _uiState.value
        if (current.game.status != GameStatus.RUNNING) return
        stopTicking()
        sounds.stopAll()
        _uiState.value = current.copy(game = current.game.copy(status = GameStatus.PAUSED))
    }

    fun turn(direction: Direction) {
        val current = _uiState.value
        _uiState.value = current.copy(game = engine.turn(current.game, direction))
    }

    fun toggleSound() {
        val enabled = !_uiState.value.soundEnabled
        scoreStore.setSoundEnabled(enabled)
        if (!enabled) sounds.stopAll()
        _uiState.value = _uiState.value.copy(soundEnabled = enabled)
    }

    private fun startNewGame() {
        stopTicking()
        val current = _uiState.value
        _uiState.value = current.copy(
            game = engine.newGame(status = GameStatus.RUNNING),
            isNewBest = false
        )
        startTicking()
    }

    private fun resume() {
        val current = _uiState.value
        if (current.game.status != GameStatus.PAUSED) return
        _uiState.value = current.copy(game = current.game.copy(status = GameStatus.RUNNING))
        startTicking()
    }

    private fun startTicking() {
        stopTicking()
        tickJob = viewModelScope.launch {
            while (isActive) {
                val game = _uiState.value.game
                if (game.status != GameStatus.RUNNING) break
                delay(game.tickMillis)
                advanceOneTick()
            }
        }
    }

    private fun advanceOneTick() {
        val current = _uiState.value
        if (current.game.status != GameStatus.RUNNING) return

        val nextGame = engine.tick(current.game)
        val ateFruit = nextGame.score > current.game.score
        val roundEnded = nextGame.status == GameStatus.GAME_OVER || nextGame.status == GameStatus.WON
        val isNewBest = roundEnded &&
            nextGame.score > current.highScore &&
            scoreStore.saveHighScore(nextGame.score)
        val highScore = if (isNewBest) nextGame.score else current.highScore

        _uiState.value = current.copy(
            game = nextGame,
            highScore = highScore,
            isNewBest = isNewBest
        )

        if (current.soundEnabled) {
            when {
                roundEnded -> sounds.playGameOver()
                ateFruit -> sounds.playEat()
            }
        }
    }

    private fun stopTicking() {
        tickJob?.cancel()
        tickJob = null
    }

    override fun onCleared() {
        stopTicking()
        sounds.release()
        super.onCleared()
    }
}

class GameViewModelFactory(private val appContext: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GameViewModel(
            scoreStore = ScoreStore(appContext),
            sounds = GameSounds(appContext)
        ) as T
    }
}

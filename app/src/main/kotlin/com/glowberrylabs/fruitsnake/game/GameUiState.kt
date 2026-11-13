package com.glowberrylabs.fruitsnake.game

data class GameUiState(
    val game: Game,
    val highScore: Int,
    val isNewBest: Boolean = false,
    val soundEnabled: Boolean = true,
    val slowMode: Boolean = false
)

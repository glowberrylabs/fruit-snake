package com.glowberrylabs.fruitsnake.game

data class GameConfig(
    val columns: Int = 16,
    val rows: Int = 16,
    val startingLength: Int = 3,
    val initialTickMillis: Long = 320L,
    val minimumTickMillis: Long = 200L,
    val speedUpEveryFruits: Int = 5,
    val speedUpStepMillis: Long = 15L
) {
    init {
        require(columns > 0) { "columns must be positive" }
        require(rows > 0) { "rows must be positive" }
        require(startingLength > 0) { "startingLength must be positive" }
        require(columns / 2 - (startingLength - 1) >= 0) { "starting snake must fit on the board" }
        require(columns.toLong() * rows > startingLength) { "board must have room for fruit" }
        require(initialTickMillis > 0) { "initialTickMillis must be positive" }
        require(minimumTickMillis > 0) { "minimumTickMillis must be positive" }
        require(minimumTickMillis <= initialTickMillis) { "minimum tick cannot exceed initial tick" }
        require(speedUpEveryFruits > 0) { "speedUpEveryFruits must be positive" }
        require(speedUpStepMillis >= 0) { "speedUpStepMillis cannot be negative" }
    }
}

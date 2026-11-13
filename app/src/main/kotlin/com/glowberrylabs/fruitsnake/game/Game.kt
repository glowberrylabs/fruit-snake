package com.glowberrylabs.fruitsnake.game

data class Game(
    val config: GameConfig,
    val snake: List<Cell>,
    val direction: Direction,
    val queuedDirections: List<Direction>,
    val fruit: Fruit,
    val fruitCell: Cell,
    val score: Int,
    val status: GameStatus
) {
    val head: Cell
        get() = snake.first()

    val boardCellCount: Int
        get() = config.columns * config.rows

    fun tickMillis(slowMode: Boolean = false): Long {
        val steps = score / config.speedUpEveryFruits
        val reduced = config.initialTickMillis - steps * config.speedUpStepMillis
        val base = reduced.coerceAtLeast(config.minimumTickMillis)
        return if (slowMode) base * SLOW_TICK_MULTIPLIER else base
    }

    private companion object {
        const val SLOW_TICK_MULTIPLIER = 2L
    }
}

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

    val tickMillis: Long
        get() {
            val steps = score / config.speedUpEveryFruits
            val reduced = config.initialTickMillis - steps * config.speedUpStepMillis
            return reduced.coerceAtLeast(config.minimumTickMillis)
        }
}

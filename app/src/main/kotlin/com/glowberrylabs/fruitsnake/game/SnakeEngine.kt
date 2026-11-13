package com.glowberrylabs.fruitsnake.game

import kotlin.random.Random

class SnakeEngine(private val config: GameConfig = GameConfig(), private val random: Random = Random.Default) {

    fun newGame(status: GameStatus = GameStatus.READY): Game {
        val headX = config.columns / 2
        val headY = config.rows / 2
        val snake = (0 until config.startingLength).map { offset ->
            Cell(headX - offset, headY)
        }
        val (fruit, fruitCell) = spawnFruit(snake)
        return Game(
            config = config,
            snake = snake,
            direction = Direction.RIGHT,
            queuedDirections = emptyList(),
            fruit = fruit,
            fruitCell = fruitCell,
            score = 0,
            status = status
        )
    }

    fun turn(game: Game, direction: Direction): Game {
        if (game.status != GameStatus.RUNNING) return game
        val lastDirection = game.queuedDirections.lastOrNull() ?: game.direction
        val canQueue = direction != lastDirection &&
            !direction.isOpposite(lastDirection) &&
            game.queuedDirections.size < MAX_QUEUED_DIRECTIONS
        return if (canQueue) {
            game.copy(queuedDirections = game.queuedDirections + direction)
        } else {
            game
        }
    }

    fun tick(game: Game): Game {
        if (game.status != GameStatus.RUNNING) return game

        val direction = game.queuedDirections.firstOrNull() ?: game.direction
        val remainingDirections = game.queuedDirections.drop(1)
        val nextHead = nextCell(game.head, direction)
        val willEat = nextHead == game.fruitCell
        val bodyToCheck = if (willEat) game.snake else game.snake.dropLast(1)
        if (nextHead == null || nextHead in bodyToCheck) {
            return game.copy(status = GameStatus.GAME_OVER)
        }

        val nextSnake = if (willEat) {
            listOf(nextHead) + game.snake
        } else {
            listOf(nextHead) + game.snake.dropLast(1)
        }
        val nextScore = if (willEat) game.score + 1 else game.score
        val advanced = game.copy(
            snake = nextSnake,
            direction = direction,
            queuedDirections = remainingDirections,
            score = nextScore
        )
        return when {
            willEat && nextSnake.size == game.boardCellCount ->
                advanced.copy(status = GameStatus.WON)

            willEat -> {
                val (fruit, fruitCell) = spawnFruit(nextSnake)
                advanced.copy(fruit = fruit, fruitCell = fruitCell)
            }

            else -> advanced
        }
    }

    private fun nextCell(head: Cell, direction: Direction): Cell? {
        val x = head.x + direction.dx
        val y = head.y + direction.dy
        val inside = x in 0 until config.columns && y in 0 until config.rows
        return if (inside) Cell(x, y) else null
    }

    private fun spawnFruit(occupied: List<Cell>): Pair<Fruit, Cell> {
        val fruitCell = requireNotNull(randomEmptyCell(occupied)) { "No empty cell for fruit" }
        return randomFruit() to fruitCell
    }

    private fun randomFruit(): Fruit = Fruit.entries[random.nextInt(Fruit.entries.size)]

    private fun randomEmptyCell(occupied: List<Cell>): Cell? {
        val occupiedCells = occupied.toSet()
        val emptyCells = buildList {
            for (y in 0 until config.rows) {
                for (x in 0 until config.columns) {
                    val cell = Cell(x, y)
                    if (cell !in occupiedCells) add(cell)
                }
            }
        }
        if (emptyCells.isEmpty()) return null
        return emptyCells[random.nextInt(emptyCells.size)]
    }

    private companion object {
        const val MAX_QUEUED_DIRECTIONS = 2
    }
}

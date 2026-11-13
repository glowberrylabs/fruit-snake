package com.glowberrylabs.fruitsnake.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeEngineTest {

    @Test
    fun newGameUsesDefaultBoard() {
        val game = SnakeEngine().newGame()
        assertEquals(16, game.config.columns)
        assertEquals(16, game.config.rows)
        assertEquals(listOf(Cell(8, 8), Cell(7, 8), Cell(6, 8)), game.snake)
        assertEquals(Direction.RIGHT, game.direction)
        assertEquals(GameStatus.READY, game.status)
        assertEquals(0, game.score)
        assertFalse(game.fruitCell in game.snake)
    }

    @Test
    fun spawnedFruitIsNeverOnSnake() {
        repeat(20) {
            val game = SnakeEngine(
                GameConfig(columns = 5, rows = 5, startingLength = 3)
            ).newGame(status = GameStatus.RUNNING)
            assertFalse(game.fruitCell in game.snake)
        }
    }

    @Test
    fun tickMovesHeadAndKeepsLength() {
        val next = engine().tick(runningGame())
        assertEquals(listOf(Cell(3, 2), Cell(2, 2), Cell(1, 2)), next.snake)
        assertEquals(Direction.RIGHT, next.direction)
        assertEquals(GameStatus.RUNNING, next.status)
        assertEquals(0, next.score)
    }

    @Test
    fun eatingFruitGrowsSnakeAndScores() {
        val next = engine().tick(
            runningGame(fruit = Fruit.BANANA, fruitCell = Cell(3, 2))
        )
        assertEquals(listOf(Cell(3, 2), Cell(2, 2), Cell(1, 2), Cell(0, 2)), next.snake)
        assertEquals(1, next.score)
        assertEquals(GameStatus.RUNNING, next.status)
        assertFalse(next.fruitCell in next.snake)
    }

    @Test
    fun queuedTurnAppliesOnNextTick() {
        val engine = engine()
        val turned = engine.turn(runningGame(), Direction.UP)
        assertEquals(listOf(Direction.UP), turned.queuedDirections)
        val next = engine.tick(turned)
        assertEquals(Cell(2, 1), next.head)
        assertEquals(Direction.UP, next.direction)
        assertTrue(next.queuedDirections.isEmpty())
    }

    @Test
    fun twoQueuedTurnsApplyInOrder() {
        val engine = engine()
        val game = engine.turn(engine.turn(runningGame(), Direction.UP), Direction.LEFT)
        assertEquals(listOf(Direction.UP, Direction.LEFT), game.queuedDirections)
        val afterFirst = engine.tick(game)
        assertEquals(Cell(2, 1), afterFirst.head)
        assertEquals(Direction.UP, afterFirst.direction)
        assertEquals(listOf(Direction.LEFT), afterFirst.queuedDirections)
        val afterSecond = engine.tick(afterFirst)
        assertEquals(Cell(1, 1), afterSecond.head)
        assertEquals(Direction.LEFT, afterSecond.direction)
        assertTrue(afterSecond.queuedDirections.isEmpty())
    }

    @Test
    fun oppositeTurnIsRejected() {
        val turned = engine().turn(runningGame(), Direction.LEFT)
        assertTrue(turned.queuedDirections.isEmpty())
    }

    @Test
    fun oppositeOfQueuedTurnIsRejected() {
        val engine = engine()
        val up = engine.turn(runningGame(), Direction.UP)
        val down = engine.turn(up, Direction.DOWN)
        assertEquals(listOf(Direction.UP), down.queuedDirections)
    }

    @Test
    fun sameDirectionIsNotQueued() {
        val turned = engine().turn(runningGame(), Direction.RIGHT)
        assertTrue(turned.queuedDirections.isEmpty())
    }

    @Test
    fun queueStopsAtTwoTurns() {
        val engine = engine()
        var game = runningGame()
        game = engine.turn(game, Direction.UP)
        game = engine.turn(game, Direction.LEFT)
        game = engine.turn(game, Direction.DOWN)
        assertEquals(listOf(Direction.UP, Direction.LEFT), game.queuedDirections)
    }

    @Test
    fun turnIsIgnoredWhenNotRunning() {
        val ready = engine().newGame()
        val turned = engine().turn(ready, Direction.UP)
        assertTrue(turned.queuedDirections.isEmpty())
    }

    @Test
    fun wallCollisionEndsGame() {
        val game = runningGame(
            snake = listOf(Cell(4, 2), Cell(3, 2), Cell(2, 2)),
            fruitCell = Cell(0, 0)
        )
        val next = engine().tick(game)
        assertEquals(GameStatus.GAME_OVER, next.status)
        assertEquals(Cell(4, 2), next.head)
    }

    @Test
    fun selfCollisionEndsGame() {
        val game = runningGame(
            snake = listOf(Cell(2, 2), Cell(2, 3), Cell(3, 3), Cell(3, 2), Cell(3, 1)),
            direction = Direction.DOWN,
            fruitCell = Cell(0, 0)
        )
        val next = engine().tick(game)
        assertEquals(GameStatus.GAME_OVER, next.status)
    }

    @Test
    fun movingIntoVacatingTailIsSafe() {
        val game = runningGame(
            snake = listOf(Cell(2, 2), Cell(1, 2), Cell(1, 3), Cell(2, 3)),
            direction = Direction.DOWN,
            fruitCell = Cell(4, 4)
        )
        val next = engine().tick(game)
        assertEquals(GameStatus.RUNNING, next.status)
        assertEquals(Cell(2, 3), next.head)
        assertEquals(listOf(Cell(2, 3), Cell(2, 2), Cell(1, 2), Cell(1, 3)), next.snake)
    }

    @Test
    fun tickIsIgnoredWhenNotRunning() {
        val ready = engine().newGame()
        assertEquals(ready, engine().tick(ready))
    }

    @Test
    fun tickMillisSpeedsUpAndClampsAtMinimum() {
        val game = runningGame()
        assertEquals(320L, game.tickMillis)
        assertEquals(305L, game.copy(score = 5).tickMillis)
        assertEquals(290L, game.copy(score = 10).tickMillis)
        assertEquals(275L, game.copy(score = 15).tickMillis)
        assertEquals(200L, game.copy(score = 1000).tickMillis)
    }

    @Test
    fun fillingTheBoardWins() {
        val config = GameConfig(columns = 2, rows = 2, startingLength = 2)
        val game = Game(
            config = config,
            snake = listOf(Cell(0, 0), Cell(0, 1), Cell(1, 1)),
            direction = Direction.RIGHT,
            queuedDirections = emptyList(),
            fruit = Fruit.CHERRY,
            fruitCell = Cell(1, 0),
            score = 2,
            status = GameStatus.RUNNING
        )
        val next = engine(config).tick(game)
        assertEquals(GameStatus.WON, next.status)
        assertEquals(4, next.snake.size)
        assertEquals(3, next.score)
    }

    private fun engine(config: GameConfig = GameConfig(columns = 5, rows = 5, startingLength = 3)): SnakeEngine =
        SnakeEngine(config)

    private fun runningGame(
        snake: List<Cell> = listOf(Cell(2, 2), Cell(1, 2), Cell(0, 2)),
        direction: Direction = Direction.RIGHT,
        fruit: Fruit = Fruit.APPLE,
        fruitCell: Cell = Cell(4, 4),
        score: Int = 0,
        queuedDirections: List<Direction> = emptyList(),
        config: GameConfig = GameConfig(columns = 5, rows = 5, startingLength = 3)
    ): Game = Game(
        config = config,
        snake = snake,
        direction = direction,
        queuedDirections = queuedDirections,
        fruit = fruit,
        fruitCell = fruitCell,
        score = score,
        status = GameStatus.RUNNING
    )
}

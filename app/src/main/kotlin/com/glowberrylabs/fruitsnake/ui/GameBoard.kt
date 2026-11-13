package com.glowberrylabs.fruitsnake.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.glowberrylabs.fruitsnake.game.Direction
import com.glowberrylabs.fruitsnake.game.Game
import com.glowberrylabs.fruitsnake.game.GameUiState

@Composable
fun GameBoard(state: GameUiState, onDirection: (Direction) -> Unit, modifier: Modifier = Modifier) {
    val game = state.game
    val fruitPainter = painterResource(game.fruit.drawableRes())
    val fruitScale = remember { Animatable(1f) }

    LaunchedEffect(game.fruitCell) {
        fruitScale.snapTo(0.45f)
        fruitScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .pointerInput(onDirection) {
                detectSwipe(onDirection)
            }
    ) {
        drawBoard(game)
        drawFruit(game, fruitPainter, fruitScale.value)
        drawSnake(game)
    }
}

private fun DrawScope.drawBoard(game: Game) {
    val cellWidth = size.width / game.config.columns
    val cellHeight = size.height / game.config.rows
    for (y in 0 until game.config.rows) {
        for (x in 0 until game.config.columns) {
            val color = if ((x + y) % 2 == 0) GamePalette.boardLight else GamePalette.boardDark
            drawRect(
                color = color,
                topLeft = Offset(x * cellWidth, y * cellHeight),
                size = Size(cellWidth, cellHeight)
            )
        }
    }
}

private fun DrawScope.drawFruit(game: Game, painter: Painter, scale: Float) {
    val cellWidth = size.width / game.config.columns
    val cellHeight = size.height / game.config.rows
    val cell = minOf(cellWidth, cellHeight)
    val center = Offset(
        (game.fruitCell.x + 0.5f) * cellWidth,
        (game.fruitCell.y + 0.5f) * cellHeight
    )
    val fruitSize = cell * 0.86f * scale

    drawOval(
        color = Color.Black.copy(alpha = 0.10f),
        topLeft = Offset(center.x - fruitSize * 0.30f, center.y + fruitSize * 0.20f),
        size = Size(fruitSize * 0.60f, fruitSize * 0.20f)
    )
    translate(left = center.x - fruitSize / 2f, top = center.y - fruitSize / 2f) {
        with(painter) {
            draw(size = Size(fruitSize, fruitSize))
        }
    }
}

private fun DrawScope.drawSnake(game: Game) {
    val cellWidth = size.width / game.config.columns
    val cellHeight = size.height / game.config.rows
    val cell = minOf(cellWidth, cellHeight)
    val lastIndex = game.snake.lastIndex.coerceAtLeast(1)

    for (index in game.snake.lastIndex downTo 1) {
        val segment = game.snake[index]
        val center = Offset((segment.x + 0.5f) * cellWidth, (segment.y + 0.5f) * cellHeight)
        val fraction = index.toFloat() / lastIndex
        drawCircle(
            color = lerp(GamePalette.snakeHead, GamePalette.snakeTail, fraction),
            radius = cell * 0.41f,
            center = center
        )
    }
    drawSnakeHead(game, cellWidth, cellHeight, cell)
}

private fun DrawScope.drawSnakeHead(game: Game, cellWidth: Float, cellHeight: Float, cell: Float) {
    val head = game.head
    val center = Offset((head.x + 0.5f) * cellWidth, (head.y + 0.5f) * cellHeight)
    val radius = cell * 0.46f
    drawCircle(color = GamePalette.snakeHead, radius = radius, center = center)

    val forward = Offset(game.direction.dx.toFloat(), game.direction.dy.toFloat())
    val side = Offset(-forward.y, forward.x)
    val eyeForward = forward * (radius * 0.25f)
    val eyeSide = side * (radius * 0.38f)
    val eyeRadius = radius * 0.24f
    val pupilRadius = eyeRadius * 0.52f
    val pupilForward = forward * (eyeRadius * 0.30f)

    val leftEye = center + eyeForward + eyeSide
    val rightEye = center + eyeForward - eyeSide
    drawCircle(color = Color.White, radius = eyeRadius, center = leftEye)
    drawCircle(color = Color.White, radius = eyeRadius, center = rightEye)
    drawCircle(color = GamePalette.snakeEye, radius = pupilRadius, center = leftEye + pupilForward)
    drawCircle(color = GamePalette.snakeEye, radius = pupilRadius, center = rightEye + pupilForward)

    val tongueStart = center + forward * (radius * 0.86f)
    val tongueEnd = center + forward * (radius * 1.52f)
    val tongueWidth = cell * 0.075f
    drawLine(
        color = GamePalette.tongue,
        start = tongueStart,
        end = tongueEnd,
        strokeWidth = tongueWidth,
        cap = StrokeCap.Round
    )
    val fork = side * (radius * 0.30f)
    val forkEnd = tongueEnd + forward * (radius * 0.24f)
    drawLine(
        color = GamePalette.tongue,
        start = tongueEnd,
        end = forkEnd + fork,
        strokeWidth = tongueWidth,
        cap = StrokeCap.Round
    )
    drawLine(
        color = GamePalette.tongue,
        start = tongueEnd,
        end = forkEnd - fork,
        strokeWidth = tongueWidth,
        cap = StrokeCap.Round
    )
}

private suspend fun PointerInputScope.detectSwipe(onDirection: (Direction) -> Unit) {
    val threshold = 28.dp.toPx()
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var total = Offset.Zero
        var fired = false
        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id && it.pressed } ?: break
            total += change.position - change.previousPosition
            if (!fired && total.getDistance() >= threshold) {
                val direction = if (kotlin.math.abs(total.x) >= kotlin.math.abs(total.y)) {
                    if (total.x > 0) Direction.RIGHT else Direction.LEFT
                } else {
                    if (total.y > 0) Direction.DOWN else Direction.UP
                }
                onDirection(direction)
                fired = true
            }
            change.consume()
        }
    }
}

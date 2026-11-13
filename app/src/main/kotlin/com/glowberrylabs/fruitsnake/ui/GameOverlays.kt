package com.glowberrylabs.fruitsnake.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.glowberrylabs.fruitsnake.R
import com.glowberrylabs.fruitsnake.game.Fruit
import com.glowberrylabs.fruitsnake.game.GameStatus
import com.glowberrylabs.fruitsnake.game.GameUiState

@Composable
fun GameOverlays(state: GameUiState, onPlay: () -> Unit, onRestart: () -> Unit, modifier: Modifier = Modifier) {
    val status = state.game.status
    Box(modifier = modifier.fillMaxSize()) {
        OverlayVisibility(visible = status == GameStatus.READY) {
            StartCard(highScore = state.highScore, onPlay = onPlay)
        }
        OverlayVisibility(visible = status == GameStatus.PAUSED) {
            PauseCard(onResume = onPlay, onRestart = onRestart)
        }
        OverlayVisibility(visible = status == GameStatus.GAME_OVER || status == GameStatus.WON) {
            RoundEndCard(
                score = state.game.score,
                highScore = state.highScore,
                isNewBest = state.isNewBest,
                won = status == GameStatus.WON,
                onReplay = onPlay
            )
        }
    }
}

@Composable
private fun OverlayVisibility(visible: Boolean, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(animationSpec = tween(180)),
            exit = fadeOut(animationSpec = tween(120))
        ) {
            OverlayScrim()
        }
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(animationSpec = tween(180)) +
                scaleIn(animationSpec = tween(180), initialScale = 0.94f),
            exit = fadeOut(animationSpec = tween(120)) +
                scaleOut(animationSpec = tween(120), targetScale = 0.96f)
        ) {
            content()
        }
    }
}

@Composable
private fun OverlayScrim() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GamePalette.scrim)
            .blockTouches()
    )
}

private fun Modifier.blockTouches(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            awaitPointerEvent().changes.forEach { it.consume() }
        }
    }
}

@Composable
private fun OverlayCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier
            .widthIn(max = 380.dp)
            .fillMaxWidth(0.92f),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
private fun StartCard(highScore: Int, onPlay: () -> Unit) {
    OverlayCard {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            color = GamePalette.snakeHead,
            textAlign = TextAlign.Center
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Fruit.entries.forEach { fruit ->
                FruitIcon(fruit = fruit, modifier = Modifier.size(38.dp))
            }
        }
        if (highScore > 0) {
            BestScoreRow(highScore = highScore)
        }
        LabeledCircleButton(
            imageVector = Icons.Filled.PlayArrow,
            label = stringResource(R.string.play),
            onClick = onPlay
        )
        Text(
            text = stringResource(R.string.start_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PauseCard(onResume: () -> Unit, onRestart: () -> Unit) {
    OverlayCard {
        Text(
            text = stringResource(R.string.paused),
            style = MaterialTheme.typography.displaySmall,
            color = GamePalette.snakeHead
        )
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            LabeledCircleButton(
                imageVector = Icons.Filled.PlayArrow,
                label = stringResource(R.string.resume),
                onClick = onResume
            )
            LabeledCircleButton(
                imageVector = Icons.Filled.Replay,
                label = stringResource(R.string.restart),
                onClick = onRestart
            )
        }
    }
}

@Composable
private fun RoundEndCard(score: Int, highScore: Int, isNewBest: Boolean, won: Boolean, onReplay: () -> Unit) {
    OverlayCard {
        Text(
            text = when {
                won -> stringResource(R.string.board_filled)
                isNewBest -> stringResource(R.string.new_best)
                else -> stringResource(R.string.nice_try)
            },
            style = MaterialTheme.typography.displaySmall,
            color = GamePalette.snakeHead,
            textAlign = TextAlign.Center
        )
        if (won || isNewBest) {
            Icon(
                imageVector = if (won) Icons.Filled.EmojiEvents else Icons.Filled.Star,
                contentDescription = null,
                tint = GamePalette.gold,
                modifier = Modifier.size(40.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FruitIcon(fruit = Fruit.APPLE, modifier = Modifier.size(44.dp))
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = GamePalette.snakeHead
            )
        }
        if (highScore > 0 && !isNewBest) {
            BestScoreRow(highScore = highScore)
        }
        LabeledCircleButton(
            imageVector = Icons.Filled.Replay,
            label = stringResource(R.string.play_again),
            onClick = onReplay
        )
    }
}

@Composable
private fun BestScoreRow(highScore: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = GamePalette.gold,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(R.string.best_score, highScore),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LabeledCircleButton(imageVector: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier
                .size(84.dp)
                .shadow(8.dp, CircleShape),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = GamePalette.snakeHead,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = label,
                modifier = Modifier.size(46.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

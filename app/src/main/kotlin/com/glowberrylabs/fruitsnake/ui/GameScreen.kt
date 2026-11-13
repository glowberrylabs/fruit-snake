package com.glowberrylabs.fruitsnake.ui

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.glowberrylabs.fruitsnake.game.Direction
import com.glowberrylabs.fruitsnake.game.Game
import com.glowberrylabs.fruitsnake.game.GameStatus
import com.glowberrylabs.fruitsnake.game.GameUiState

@Composable
fun GameScreen(
    state: GameUiState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onRestart: () -> Unit,
    onDirection: (Direction) -> Unit,
    onToggleSound: () -> Unit,
    onToggleSlowMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val game = state.game
    val overlayVisible = game.status != GameStatus.RUNNING

    GameScreenEffects(game = game, onPause = onPause)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GamePalette.skyTop, GamePalette.skyBottom)
                )
            )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .then(
                    if (overlayVisible) {
                        Modifier.semantics { hideFromAccessibility() }
                    } else {
                        Modifier
                    }
                )
        ) {
            if (maxWidth > maxHeight) {
                LandscapeLayout(
                    state = state,
                    onPause = onPause,
                    onDirection = onDirection,
                    onToggleSound = onToggleSound,
                    onToggleSlowMode = onToggleSlowMode
                )
            } else {
                PortraitLayout(
                    state = state,
                    onPause = onPause,
                    onDirection = onDirection,
                    onToggleSound = onToggleSound,
                    onToggleSlowMode = onToggleSlowMode
                )
            }
        }
        GameOverlays(
            state = state,
            onPlay = onPlay,
            onRestart = onRestart,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun GameScreenEffects(game: Game, onPause: () -> Unit) {
    val view = LocalView.current

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        onPause()
    }
    BackHandler(enabled = game.status == GameStatus.RUNNING) {
        onPause()
    }
    DisposableEffect(game.status) {
        view.keepScreenOn = game.status == GameStatus.RUNNING
        onDispose { view.keepScreenOn = false }
    }
    LaunchedEffect(game.score) {
        if (game.score > 0) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }
    LaunchedEffect(game.status) {
        if (game.status == GameStatus.GAME_OVER || game.status == GameStatus.WON) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }
}

@Composable
private fun PortraitLayout(
    state: GameUiState,
    onPause: () -> Unit,
    onDirection: (Direction) -> Unit,
    onToggleSound: () -> Unit,
    onToggleSlowMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameHud(
            score = state.game.score,
            soundEnabled = state.soundEnabled,
            slowMode = state.slowMode,
            pauseEnabled = state.game.status == GameStatus.RUNNING,
            onToggleSound = onToggleSound,
            onToggleSlowMode = onToggleSlowMode,
            onPause = onPause,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        BoardArea(
            state = state,
            onDirection = onDirection,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Spacer(Modifier.height(12.dp))
        Dpad(
            enabled = state.game.status == GameStatus.RUNNING,
            onDirection = onDirection
        )
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun LandscapeLayout(
    state: GameUiState,
    onPause: () -> Unit,
    onDirection: (Direction) -> Unit,
    onToggleSound: () -> Unit,
    onToggleSlowMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoardArea(
            state = state,
            onDirection = onDirection,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GameHud(
                score = state.game.score,
                soundEnabled = state.soundEnabled,
                slowMode = state.slowMode,
                pauseEnabled = state.game.status == GameStatus.RUNNING,
                onToggleSound = onToggleSound,
                onToggleSlowMode = onToggleSlowMode,
                onPause = onPause,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(28.dp))
            Dpad(
                enabled = state.game.status == GameStatus.RUNNING,
                onDirection = onDirection
            )
        }
    }
}

@Composable
private fun BoardArea(state: GameUiState, onDirection: (Direction) -> Unit, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val columns = state.game.config.columns
        val rows = state.game.config.rows
        val ratio = columns.toFloat() / rows.toFloat()
        val boardWidth = minOf(maxWidth, maxHeight * ratio)
        val boardHeight = boardWidth / ratio

        Surface(
            modifier = Modifier.size(boardWidth, boardHeight),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            GameBoard(
                state = state,
                onDirection = onDirection,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .clip(RoundedCornerShape(22.dp))
            )
        }
    }
}

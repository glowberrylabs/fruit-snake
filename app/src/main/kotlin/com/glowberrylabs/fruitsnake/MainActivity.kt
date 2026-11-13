package com.glowberrylabs.fruitsnake

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glowberrylabs.fruitsnake.game.GameViewModel
import com.glowberrylabs.fruitsnake.game.GameViewModelFactory
import com.glowberrylabs.fruitsnake.ui.FruitSnakeTheme
import com.glowberrylabs.fruitsnake.ui.GameScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.argb(0x80, 0, 0, 0)
            )
        )
        setContent {
            FruitSnakeTheme {
                val viewModel: GameViewModel = viewModel(
                    factory = GameViewModelFactory(applicationContext)
                )
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                GameScreen(
                    state = state,
                    onPlay = viewModel::play,
                    onPause = viewModel::pause,
                    onRestart = viewModel::restart,
                    onDirection = viewModel::turn,
                    onToggleSound = viewModel::toggleSound,
                    onToggleSlowMode = viewModel::toggleSlowMode
                )
            }
        }
    }
}

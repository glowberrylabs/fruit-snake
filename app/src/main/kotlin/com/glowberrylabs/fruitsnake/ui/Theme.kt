package com.glowberrylabs.fruitsnake.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFFF9A825),
    onSecondary = Color(0xFF3E2723),
    background = Color(0xFFEAF8FF),
    onBackground = Color(0xFF20313A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF20313A),
    surfaceVariant = Color(0xFFDDF1DA),
    onSurfaceVariant = Color(0xFF40534A)
)

@Composable
fun FruitSnakeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}

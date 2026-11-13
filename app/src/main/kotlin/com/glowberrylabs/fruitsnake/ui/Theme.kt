package com.glowberrylabs.fruitsnake.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

private val LightColors = lightColorScheme(
    primary = GamePalette.snakeHead,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = GamePalette.gold,
    onSecondary = Color(0xFF3E2723),
    background = Color(0xFFEAF8FF),
    onBackground = GamePalette.ink,
    surface = Color.White,
    onSurface = GamePalette.ink,
    surfaceVariant = Color(0xFFDDF1DA),
    onSurfaceVariant = Color(0xFF40534A)
)

private val BaseTypography = Typography()

private val AppTypography = Typography(
    displaySmall = BaseTypography.displaySmall.copy(fontWeight = FontWeight.Black),
    headlineMedium = BaseTypography.headlineMedium.copy(fontWeight = FontWeight.Black),
    headlineSmall = BaseTypography.headlineSmall.copy(fontWeight = FontWeight.Black),
    titleLarge = BaseTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = BaseTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
    titleSmall = BaseTypography.titleSmall.copy(fontWeight = FontWeight.Bold)
)

@Composable
fun FruitSnakeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}

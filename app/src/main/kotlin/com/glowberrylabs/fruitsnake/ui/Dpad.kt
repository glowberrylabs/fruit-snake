package com.glowberrylabs.fruitsnake.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.glowberrylabs.fruitsnake.R
import com.glowberrylabs.fruitsnake.game.Direction

private val buttonSize = 60.dp
private val buttonGap = 6.dp

@Composable
fun Dpad(enabled: Boolean, onDirection: (Direction) -> Unit, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(buttonGap)
        ) {
            DirectionButton(
                direction = Direction.UP,
                icon = Icons.Filled.KeyboardArrowUp,
                contentDescription = stringResource(R.string.direction_up),
                enabled = enabled,
                onDirection = onDirection
            )
            Row(horizontalArrangement = Arrangement.spacedBy(buttonGap)) {
                DirectionButton(
                    direction = Direction.LEFT,
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.direction_left),
                    enabled = enabled,
                    onDirection = onDirection
                )
                Spacer(Modifier.size(buttonSize))
                DirectionButton(
                    direction = Direction.RIGHT,
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.direction_right),
                    enabled = enabled,
                    onDirection = onDirection
                )
            }
            DirectionButton(
                direction = Direction.DOWN,
                icon = Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(R.string.direction_down),
                enabled = enabled,
                onDirection = onDirection
            )
        }
    }
}

@Composable
private fun DirectionButton(
    direction: Direction,
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onDirection: (Direction) -> Unit
) {
    FilledIconButton(
        onClick = { onDirection(direction) },
        enabled = enabled,
        modifier = Modifier
            .size(buttonSize)
            .shadow(3.dp, CircleShape),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.White,
            contentColor = GamePalette.snakeHead,
            disabledContainerColor = Color.White.copy(alpha = 0.65f),
            disabledContentColor = GamePalette.snakeHead.copy(alpha = 0.35f)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(36.dp)
        )
    }
}

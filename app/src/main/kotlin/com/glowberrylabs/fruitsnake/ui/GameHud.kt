package com.glowberrylabs.fruitsnake.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.glowberrylabs.fruitsnake.R
import com.glowberrylabs.fruitsnake.game.Fruit

@Composable
fun GameHud(
    score: Int,
    soundEnabled: Boolean,
    pauseEnabled: Boolean,
    onToggleSound: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScorePill(score = score)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HudIconButton(
                imageVector = if (soundEnabled) {
                    Icons.AutoMirrored.Filled.VolumeUp
                } else {
                    Icons.AutoMirrored.Filled.VolumeOff
                },
                contentDescription = stringResource(
                    if (soundEnabled) R.string.mute_sound else R.string.enable_sound
                ),
                onClick = onToggleSound
            )
            HudIconButton(
                imageVector = Icons.Filled.Pause,
                contentDescription = stringResource(R.string.pause),
                onClick = onPause,
                enabled = pauseEnabled
            )
        }
    }
}

@Composable
private fun ScorePill(score: Int) {
    val scale = remember { Animatable(1f) }
    val scoreDescription = pluralStringResource(R.plurals.score_description, score, score)

    LaunchedEffect(score) {
        if (score > 0) {
            scale.snapTo(1.28f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        }
    }

    Surface(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .shadow(4.dp, RoundedCornerShape(percent = 50)),
        shape = RoundedCornerShape(percent = 50),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .clearAndSetSemantics { contentDescription = scoreDescription }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FruitIcon(fruit = Fruit.APPLE, modifier = Modifier.size(26.dp))
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = GamePalette.snakeHead
            )
        }
    }
}

@Composable
private fun HudIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(52.dp)
            .shadow(4.dp, CircleShape),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.White,
            contentColor = GamePalette.snakeHead,
            disabledContainerColor = Color.White.copy(alpha = 0.65f),
            disabledContentColor = GamePalette.snakeHead.copy(alpha = 0.35f)
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(26.dp)
        )
    }
}

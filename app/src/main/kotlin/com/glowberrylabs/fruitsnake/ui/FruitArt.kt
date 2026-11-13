package com.glowberrylabs.fruitsnake.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.glowberrylabs.fruitsnake.R
import com.glowberrylabs.fruitsnake.game.Fruit

@DrawableRes
fun Fruit.drawableRes(): Int = when (this) {
    Fruit.APPLE -> R.drawable.ic_fruit_apple
    Fruit.BANANA -> R.drawable.ic_fruit_banana
    Fruit.CHERRY -> R.drawable.ic_fruit_cherry
}

@Composable
fun FruitIcon(fruit: Fruit, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(fruit.drawableRes()),
        contentDescription = null,
        modifier = modifier
    )
}

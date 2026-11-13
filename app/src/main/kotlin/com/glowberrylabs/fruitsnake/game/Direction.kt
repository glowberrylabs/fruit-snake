package com.glowberrylabs.fruitsnake.game

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    fun isOpposite(other: Direction): Boolean = dx + other.dx == 0 && dy + other.dy == 0
}

package ru.spbau.mit.roguelike.world

import ru.spbau.mit.roguelike.util.squareAreaCoordinates
import java.util.*


/**
 * Data class that represents a point in 3D-space.
 */
data class Point(val x: Int, val y: Int, val z: Int)

fun Point.neighbors8(): MutableList<Point> {
    return squareAreaCoordinates(radius = 1)
            .filter { (ox, oy) -> ox != 0 && oy != 0 }
            .map { (ox, oy) -> this.copy(x = this.x + ox, y = this.y + oy) }
            .shuffled()
            .toMutableList()
}
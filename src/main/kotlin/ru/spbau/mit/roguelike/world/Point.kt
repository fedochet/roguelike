package ru.spbau.mit.roguelike.world

import ru.spbau.mit.roguelike.util.squareAreaCoordinates
import java.util.*


data class Point(@JvmField val x: Int, @JvmField val y: Int, @JvmField val z: Int)

fun Point.neighbors8(): List<Point> {
    val points = ArrayList<Point>()

    for ((ox, oy) in squareAreaCoordinates(radius = 1)) {
        if (ox == 0 && oy == 0)
            continue

        points.add(Point(x + ox, y + oy, z))
    }

    points.shuffle()
    return points
}
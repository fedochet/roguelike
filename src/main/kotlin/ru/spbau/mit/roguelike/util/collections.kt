package ru.spbau.mit.roguelike.util

infix fun <T, K> Iterable<T>.product(other: Iterable<K>) = this.flatMap { i -> other.map { i to it } }

fun squareAreaCoordinates(radius: Int) = ((-radius..radius) product (-radius..radius))

fun roundAreaCoordinates(radius: Int) = squareAreaCoordinates(radius).filter { (x, y) -> x*x + y*y <= radius*radius }
/**
 * @return [this] if it is in range `[from, to]`, or closest to it value from range otherwise.
 */
fun Int.keepInRange(from: Int, to: Int): Int {
    return Math.max(from, Math.min(this, to))
}
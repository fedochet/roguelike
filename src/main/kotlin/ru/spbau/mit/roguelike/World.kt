package ru.spbau.mit.roguelike

import ru.spbau.mit.roguelike.Tile.*
import ru.spbau.mit.roguelike.entities.Creature
import ru.spbau.mit.roguelike.util.product

class World(private val tiles: Array<Array<Tile>>) {
    val width = tiles.size
    val height = tiles[0].size

    val creatures: MutableList<Creature> = mutableListOf()

    fun getGlyph(x: Int, y: Int) = getTile(x, y).glyph
    fun getColor(x: Int, y: Int) = getTile(x, y).color

    fun getTile(x: Int, y: Int) = tiles.getOrNull(x)?.getOrNull(y) ?: BOUNDS

    fun dig(x: Int, y: Int) {
        if (getTile(x, y).diggable) {
            tiles[x][y] = FLOOR
        }
    }

    fun addToEmptyLocation(creature: Creature) {
        while (true) {
            val x = (Math.random() * width).toInt()
            val y = (Math.random() * height).toInt()

            if (getTile(x, y).steppable && getCreatureAt(x, y) == null) {
                creatures.add(creature)
                creature.x = x
                creature.y = y
                break
            }
        }
    }

    fun getCreatureAt(x: Int, y: Int) = creatures.find { it.x == x && it.y == y }
    fun removeCreature(creature: Creature) = creatures.remove(creature)
}

class WorldBuilder(private val width: Int, private val height: Int) {
    private var tiles = Array(width, { Array(height, { BOUNDS }) })

    private val mapCoordinates = (0 until width) product (0 until height)

    fun build() = World(tiles)

    fun makeCaves(): WorldBuilder = randomizeTiles().smooth(8)

    private fun randomizeTiles(): WorldBuilder {

        for ((x, y) in mapCoordinates) {
            tiles[x][y] = if (Math.random() > 0.5) FLOOR else WALL
        }

        return this
    }

    private fun smooth(times: Int): WorldBuilder {
        val tempTiles = Array(width, { Array(height, { BOUNDS }) })

        repeat(times) {
            for ((x, y) in mapCoordinates) {
                var floors = 0
                var rocks = 0

                for ((ox, oy) in -1..1 product -1..1) {
                    if (!validCoordinates(x + ox, y + oy))
                        continue

                    if (tiles[x + ox][y + oy] == FLOOR) {
                        floors++
                    } else {
                        rocks++
                    }
                }

                tempTiles[x][y] = if (floors >= rocks) FLOOR else WALL
            }

            tiles = tempTiles
        }

        return this
    }

    private fun validCoordinates(x: Int, y: Int): Boolean {
        return (x in 0 until width) && (y in 0 until height)
    }
}

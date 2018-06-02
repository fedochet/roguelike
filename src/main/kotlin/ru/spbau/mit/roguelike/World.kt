package ru.spbau.mit.roguelike

import ru.spbau.mit.roguelike.util.product

class World(private val tiles: Array<Array<Tile>>) {
    val width = tiles.size
    val height = tiles[0].size

    fun getGlyph(x: Int, y: Int) = getTile(x, y).glyph
    fun getColor(x: Int, y: Int) = getTile(x, y).color

    private fun getTile(x: Int, y: Int) = tiles.getOrNull(x)?.getOrNull(y) ?: Tile.BOUNDS
}

class WorldBuilder(private val width: Int, private val height: Int) {
    private var tiles = Array(width, { Array(height, { Tile.BOUNDS }) })

    private val mapCoordinates = (0 until width) product (0 until height)

    fun build() = World(tiles)

    fun makeCaves(): WorldBuilder = randomizeTiles().smooth(8)

    private fun randomizeTiles(): WorldBuilder {

        for ((x, y) in mapCoordinates) {
            tiles[x][y] = if (Math.random() > 0.5) Tile.FLOOR else Tile.WALL
        }

        return this
    }

    private fun smooth(times: Int): WorldBuilder {
        val tempTiles = Array(width, { Array(height, { Tile.BOUNDS }) })

        repeat(times) {
            for ((x, y) in mapCoordinates) {
                var floors = 0
                var rocks = 0

                for ((ox, oy) in -1..1 product -1..1) {
                    if (!validCoordinates(x + ox, y + oy))
                        continue

                    if (tiles[x + ox][y + oy] == Tile.FLOOR) {
                        floors++
                    } else {
                        rocks++
                    }
                }

                tempTiles[x][y] = if (floors >= rocks) Tile.FLOOR else Tile.WALL
            }

            tiles = tempTiles
        }

        return this
    }

    private fun validCoordinates(x: Int, y: Int): Boolean {
        return (x in 0 until width) && (y in 0 until height)
    }
}

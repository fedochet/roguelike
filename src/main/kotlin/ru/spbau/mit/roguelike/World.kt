package ru.spbau.mit.roguelike

class World(private val tiles: Array<Array<Tile>>) {
    val width = tiles.size
    val height = tiles[0].size

    fun getGlyph(x: Int, y: Int) = getTile(x, y).glyph
    fun getColor(x: Int, y: Int) = getTile(x, y).color

    private fun getTile(x: Int, y: Int) = tiles.getOrNull(x)?.getOrNull(y) ?: Tile.BOUNDS
}

class WorldBuilder(private val width: Int, private val height: Int) {
    private var tiles = Array(width, { Array(height, { Tile.BOUNDS }) })

    fun build() = World(tiles)

    fun makeCaves(): WorldBuilder = randomizeTiles().smooth(8)

    private fun randomizeTiles(): WorldBuilder {
        for (i in 0 until width) {
            for (j in 0 until height) {
                tiles[i][j] = if (Math.random() > 0.5) Tile.FLOOR else Tile.WALL
            }
        }

        return this
    }

    private fun smooth(times: Int): WorldBuilder {
        val tempTiles = Array(width, { Array(height, { Tile.BOUNDS }) })

        for (time in 0 until times) {

            for (x in 0 until width) {
                for (y in 0 until height) {
                    var floors = 0
                    var rocks = 0

                    for (ox in -1..1) {
                        for (oy in -1..1) {
                            if (x + ox < 0 || x + ox >= width || y + oy < 0 || y + oy >= height)
                                continue

                            if (tiles[x + ox][y + oy] == Tile.FLOOR) {
                                floors++
                            } else {
                                rocks++
                            }
                        }
                    }

                    tempTiles[x][y] = if (floors >= rocks) Tile.FLOOR else Tile.WALL
                }
            }
            tiles = tempTiles
        }

        return this
    }

}
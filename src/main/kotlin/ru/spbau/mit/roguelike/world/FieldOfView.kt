package ru.spbau.mit.roguelike.world

import ru.spbau.mit.roguelike.Tile

class FieldOfView(private val world: World) {
    private var depth: Int = 0
    private var visible = Array(world.width) { BooleanArray(world.height) { false } }
    private val tiles = Array(world.width) { Array(world.height) { Array(world.depth) { Tile.UNKNOWN } } }

    fun isVisible(x: Int, y: Int, z: Int): Boolean {
        return z == depth && x >= 0 && y >= 0 && x < visible.size && y < visible[0].size && visible[x][y]
    }

    fun tile(x: Int, y: Int, z: Int): Tile {
        return tiles[x][y][z]
    }

    fun update(wx: Int, wy: Int, wz: Int, r: Int) {
        depth = wz
        visible = Array(world.width) { BooleanArray(world.height) }

        for (x in -r until r) {
            for (y in -r until r) {
                if (x * x + y * y > r * r)
                    continue

                if (!validCoordinate(wx + x, wy + y))
                    continue

                for ((x1, y1) in Line.create(wx, wy, wx + x, wy + y)) {
                    val tile = world.getTile(x1, y1, wz)
                    visible[x1][y1] = true
                    tiles[x1][y1][wz] = tile

                    if (!tile.steppable)
                        break
                }
            }
        }
    }

    private fun validCoordinate(xx: Int, yy: Int): Boolean {
        return xx in 0 until world.width
            && yy in 0 until world.height
    }
}


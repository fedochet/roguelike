package ru.spbau.mit.roguelike.world

import ru.spbau.mit.roguelike.Tile
import ru.spbau.mit.roguelike.Tile.*
import ru.spbau.mit.roguelike.entities.Creature

class World(private val tiles: Array<Array<Array<Tile>>>) {
    val width = tiles.size
    val height = tiles[0].size
    val depth = tiles[0][0].size

    val creatures: MutableList<Creature> = mutableListOf()

    fun getGlyph(x: Int, y: Int, z: Int) = getCreatureAt(x, y, z)?.glyph ?: getTile(x, y, z).glyph
    fun getColor(x: Int, y: Int, z: Int) = getCreatureAt(x, y, z)?.color ?: getTile(x, y, z).color

    fun getTile(x: Int, y: Int, z: Int) = tiles.getOrNull(x)?.getOrNull(y)?.getOrNull(z) ?: BOUNDS

    fun dig(x: Int, y: Int, z: Int) {
        if (getTile(x, y, z).diggable) {
            tiles[x][y][z] = FLOOR
        }
    }

    fun addToEmptyLocation(creature: Creature) {
        while (true) {
            val x = (Math.random() * width).toInt()
            val y = (Math.random() * height).toInt()
            val z = (Math.random() * depth).toInt()

            if (getTile(x, y, z).steppable && getCreatureAt(x, y, z) == null) {
                creatures.add(creature)
                creature.x = x
                creature.y = y
                creature.z = z
                break
            }
        }
    }

    fun getCreatureAt(x: Int, y: Int, z: Int) = creatures.find { it.x == x && it.y == y && it.z == z }
    fun removeCreature(creature: Creature) = creatures.remove(creature)
}

//class WorldBuilder(private val width: Int, private val height: Int, private val depth: Int) {
//    private var nextRegion = 1
//    private var tiles = Array(width) { Array(height) { Array(depth) { BOUNDS } } }
//    private var regions = Array(width) { Array(height) { IntArray(depth) { 0 } } }
//
//    private val mapCoordinates = (0 until width).product(0 until height).product(0 until depth).map { (xy, z) ->
//        val (x, y) = xy
//        Point(x, y, z)
//    }
//
//    fun build() = World(tiles)
//
//    fun makeCaves(): WorldBuilder = randomizeTiles().smooth(8).createRegions().connectRegions();
//
//
//    private fun randomizeTiles(): WorldBuilder {
//
//        for ((x, y, z) in mapCoordinates) {
//            tiles[x][y][z] = if (Math.random() > 0.5) FLOOR else WALL
//        }
//
//        return this
//    }
//
//    private fun smooth(times: Int): WorldBuilder {
//        val tempTiles = Array(width) { Array(height) { Array(depth) { BOUNDS } } }
//
//        repeat(times) {
//            for ((x, y, z) in mapCoordinates) {
//                var floors = 0
//                var rocks = 0
//
//                for ((ox, oy) in squareAreaCoordinates(radius = 1)) {
//                    if (!validCoordinates(x + ox, y + oy))
//                        continue
//
//                    if (tiles[x + ox][y + oy][z] == FLOOR) {
//                        floors++
//                    } else {
//                        rocks++
//                    }
//                }
//
//                tempTiles[x][y][z] = if (floors >= rocks) FLOOR else WALL
//            }
//
//            tiles = tempTiles
//        }
//
//        return this
//    }
//
//    private fun createRegions(): WorldBuilder {
//        regions = Array(width) { Array(height) { IntArray(depth) { 0 } } }
//
//        for (z in 0 until depth) {
//            for (x in 0 until width) {
//                for (y in 0 until height) {
//                    if (tiles[x][y][z] !== Tile.WALL && regions[x][y][z] === 0) {
//                        val size = fillRegion(nextRegion++, x, y, z)
//
//                        if (size < 25) {
//                            removeRegion(nextRegion - 1, z)
//                        }
//                    }
//                }
//            }
//        }
//        return this
//    }
//
//    private fun removeRegion(region: Int, z: Int) {
//        for (x in 0 until width) {
//            for (y in 0 until height) {
//                if (regions[x][y][z] == region) {
//                    regions[x][y][z] = 0
//                    tiles[x][y][z] = Tile.WALL
//                }
//            }
//        }
//    }
//
//    private fun fillRegion(region: Int, x: Int, y: Int, z: Int): Int {
//        var size = 1
//        val open = ArrayList<Point>()
//        open.add(Point(x, y, z))
//        regions[x][y][z] = region
//
//        while (!open.isEmpty()) {
//            val p = open.removeAt(0)
//
//            for (neighbor in p.neighbors8()) {
//                val region = regions[p] ?: continue
//                val tile = tiles[p] ?: continue
//
//                if (region > 0 || tile === Tile.WALL)
//                    continue
//
//                size++
//                regions[neighbor.x][neighbor.y][neighbor.z] = region
//                open.add(neighbor)
//            }
//        }
//
//        return size
//    }
//
//
//    fun connectRegions(): WorldBuilder {
//        for (z in 0..(depth - 2)) {
//            connectRegionsDown(z)
//        }
//        return this
//    }
//
//    private fun connectRegionsDown(z: Int) {
//        val connected = ArrayList<String>()
//
//        for (x in 0 until width) {
//            for (y in 0 until height) {
//                val region = regions[x][y][z].toString() + "," + regions[x][y][z + 1]
//                if (tiles[x][y][z] === Tile.FLOOR
//                        && tiles[x][y][z + 1] === Tile.FLOOR
//                        && !connected.contains(region)) {
//                    connected.add(region)
//                    connectRegionsDown(z, regions[x][y][z], regions[x][y][z + 1])
//                }
//            }
//        }
//    }
//
//    private fun connectRegionsDown(z: Int, r1: Int, r2: Int) {
//        val candidates = findRegionOverlaps(z, r1, r2)
//
//        var stairs = 0
//        do {
//            val p = candidates.removeAt(0)
//            tiles[p.x][p.y][z] = Tile.STAIRS_DOWN
//            tiles[p.x][p.y][z + 1] = Tile.STAIRS_UP
//            stairs++
//        } while (candidates.size / stairs > 250)
//    }
//
//
//    private fun findRegionOverlaps(z: Int, r1: Int, r2: Int): MutableList<Point> {
//        val candidates = mutableListOf<Point>()
//
//        for (x in 0 until width) {
//            for (y in 0 until height) {
//                if (tiles[x][y][z] === Tile.FLOOR
//                        && tiles[x][y][z + 1] === Tile.FLOOR
//                        && regions[x][y][z] == r1
//                        && regions[x][y][z + 1] == r2) {
//                    candidates.add(Point(x, y, z))
//                }
//            }
//        }
//
//        candidates.shuffle()
//        return candidates
//    }
//
//
//    private fun validCoordinates(x: Int, y: Int): Boolean {
//        return (x in 0 until width) && (y in 0 until height)
//    }
//}

private operator fun <T> Array<Array<Array<T>>>.get(p: Point) = this.getOrNull(p.x)?.getOrNull(p.y)?.getOrNull(p.z)
private operator fun Array<Array<IntArray>>.get(p: Point) = this.getOrNull(p.x)?.getOrNull(p.y)?.getOrNull(p.z)
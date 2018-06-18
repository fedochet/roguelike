package ru.spbau.mit.roguelike.world

import java.util.*

/**
 * Class to create random world with 3D-caves and stairs between levels.
 */
class WorldBuilder(private val width: Int, private val height: Int, private val depth: Int) {
    private var tiles: Array<Array<Array<Tile>>>
    private var regions: Array<Array<IntArray>>
    private var nextRegion: Int = 0

    init {
        this.tiles = Array(width) { Array(height) { Array(depth) { Tile.BOUNDS } } }
        this.regions = Array(width) { Array(height) { IntArray(depth) { 0 } } }
        this.nextRegion = 1
    }

    fun build(): World {
        return World(tiles)
    }

    private fun randomizeTiles(): WorldBuilder {
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    tiles[x][y][z] = if (Math.random() < 0.5) Tile.FLOOR else Tile.WALL
                }
            }
        }
        return this
    }

    private fun smooth(times: Int): WorldBuilder {
        val tiles2 = Array(width) { Array(height) { Array(depth) { Tile.BOUNDS } } }
        for (time in 0 until times) {

            for (x in 0 until width) {
                for (y in 0 until height) {
                    for (z in 0 until depth) {
                        var floors = 0
                        var rocks = 0

                        for (ox in -1..1) {
                            for (oy in -1..1) {
                                if (!validCoordinate(x + ox, y + oy))
                                    continue

                                if (tiles[x + ox][y + oy][z] === Tile.FLOOR)
                                    floors++
                                else
                                    rocks++
                            }
                        }
                        tiles2[x][y][z] = if (floors >= rocks) Tile.FLOOR else Tile.WALL
                    }
                }
            }
            tiles = tiles2
        }
        return this
    }

    private fun validCoordinate(xx: Int, yy: Int): Boolean {
        return (xx in 0..(width - 1))
            && (yy in 0..(height - 1))
    }

    private fun createRegions(): WorldBuilder {
        regions = Array(width) { Array(height) { IntArray(depth) } }

        for (z in 0 until depth) {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (tiles[x][y][z] !== Tile.WALL && regions[x][y][z] == 0) {
                        val size = fillRegion(nextRegion++, x, y, z)

                        if (size < 25)
                            removeRegion(nextRegion - 1, z)
                    }
                }
            }
        }

        return this
    }

    private fun removeRegion(region: Int, z: Int) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (regions[x][y][z] == region) {
                    regions[x][y][z] = 0
                    tiles[x][y][z] = Tile.WALL
                }
            }
        }
    }

    private fun fillRegion(region: Int, x: Int, y: Int, z: Int): Int {
        var size = 1
        val open = ArrayList<Point>()
        open.add(Point(x, y, z))
        regions[x][y][z] = region

        while (!open.isEmpty()) {
            val p = open.removeAt(0)

            for (neighbor in p.neighbors8()) {
                if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= width || neighbor.y >= height)
                    continue

                if (regions[neighbor.x][neighbor.y][neighbor.z] > 0 || tiles[neighbor.x][neighbor.y][neighbor.z] === Tile.WALL)
                    continue

                size++
                regions[neighbor.x][neighbor.y][neighbor.z] = region
                open.add(neighbor)
            }
        }
        return size
    }

    fun connectRegions(): WorldBuilder {
        for (z in 0 until depth - 1) {
            connectRegionsDown(z)
        }
        return this
    }

    private fun connectRegionsDown(z: Int) {
        val connected = ArrayList<String>()

        for (x in 0 until width) {
            for (y in 0 until height) {
                val region = regions[x][y][z].toString() + "," + regions[x][y][z + 1]
                if (tiles[x][y][z] === Tile.FLOOR && tiles[x][y][z + 1] === Tile.FLOOR && !connected.contains(region)) {
                    connected.add(region)
                    connectRegionsDown(z, regions[x][y][z], regions[x][y][z + 1])
                }
            }
        }
    }

    private fun connectRegionsDown(z: Int, r1: Int, r2: Int) {
        val candidates = findRegionOverlaps(z, r1, r2)

        var stairs = 0
        do {
            val (x, y) = candidates.removeAt(0)
            tiles[x][y][z] = Tile.STAIRS_DOWN
            tiles[x][y][z + 1] = Tile.STAIRS_UP
            stairs++
        } while (candidates.size / stairs > 250)
    }

    private fun findRegionOverlaps(z: Int, r1: Int, r2: Int): MutableList<Point> {
        val candidates = ArrayList<Point>()

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (tiles[x][y][z] === Tile.FLOOR
                        && tiles[x][y][z + 1] === Tile.FLOOR
                        && regions[x][y][z] == r1
                        && regions[x][y][z + 1] == r2) {
                    candidates.add(Point(x, y, z))
                }
            }
        }

        candidates.shuffle()
        return candidates
    }

    private fun addExitStairs(): WorldBuilder {
        var x: Int
        var y: Int

        do {
            x = (Math.random() * width).toInt()
            y = (Math.random() * height).toInt()
        } while (tiles[x][y][0] !== Tile.FLOOR)

        tiles[x][y][0] = Tile.STAIRS_UP
        return this
    }


    fun makeCaves(): WorldBuilder {
        return randomizeTiles()
                .smooth(8)
                .createRegions()
                .connectRegions()
                .addExitStairs()
    }
}
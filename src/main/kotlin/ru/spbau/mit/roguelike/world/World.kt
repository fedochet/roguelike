package ru.spbau.mit.roguelike.world

import ru.spbau.mit.roguelike.Tile
import ru.spbau.mit.roguelike.Tile.BOUNDS
import ru.spbau.mit.roguelike.Tile.FLOOR
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

    fun update() {
        for (creature in creatures.toList()) {
            creature.update()
        }
    }
}

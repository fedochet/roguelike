package ru.spbau.mit.roguelike.world

import ru.spbau.mit.roguelike.Tile
import ru.spbau.mit.roguelike.Tile.BOUNDS
import ru.spbau.mit.roguelike.Tile.FLOOR
import ru.spbau.mit.roguelike.entities.Creature
import java.awt.Color
import java.util.*


class World(private val tiles: Array<Array<Array<Tile>>>) {
    val width = tiles.size
    val height = tiles[0].size
    val depth = tiles[0][0].size

    val items = Array(width) { Array(height) { arrayOfNulls<Item>(depth)}}
    val creatures: MutableList<Creature> = mutableListOf()

    fun getGlyph(x: Int, y: Int, z: Int): Char {
        return getCreatureAt(x, y, z)?.glyph
                ?: getItem(x, y, z)?.glyph
                ?: getTile(x, y, z).glyph
    }
    fun getColor(x: Int, y: Int, z: Int): Color {
        return getCreatureAt(x, y, z)?.color
                ?: getItem(x, y, z)?.color
                ?: getTile(x, y, z).color
    }
    fun getItem(x: Int, y: Int, z: Int): Item? = items[x][y][z]

    fun getTile(x: Int, y: Int, z: Int) = tiles.getOrNull(x)?.getOrNull(y)?.getOrNull(z) ?: BOUNDS

    fun dig(x: Int, y: Int, z: Int) {
        if (getTile(x, y, z).diggable) {
            tiles[x][y][z] = FLOOR
        }
    }

    fun addToEmptyLocation(creature: Creature, z: Int) {
        while (true) {
            val x = (Math.random() * width).toInt()
            val y = (Math.random() * height).toInt()

            if (getTile(x, y, z).steppable && getCreatureAt(x, y, z) == null) {
                creatures.add(creature)
                creature.x = x
                creature.y = y
                creature.z = z
                break
            }
        }
    }

    fun addToEmptyLocation(item: Item, z: Int) {
        while (true) {
            val x = (Math.random() * width).toInt()
            val y = (Math.random() * height).toInt()

            if (getTile(x, y, z).steppable && getItem(x, y, z) == null) {
                items[x][y][z] = item
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

    fun removeItem(x: Int, y: Int, z: Int) {
        items[x][y][z] = null
    }

    fun addAtEmptySpace(item: Item?, x: Int, y: Int, z: Int) {
        if (item == null)
            return

        val points = ArrayList<Point>()
        val checked = ArrayList<Point>()

        points.add(Point(x, y, z))

        while (!points.isEmpty()) {
            val p = points.removeAt(0)
            checked.add(p)

            if (!getTile(p.x, p.y, p.z).steppable)
                continue

            if (items[p.x][p.y][p.z] == null) {
                items[p.x][p.y][p.z] = item
                val c = this.getCreatureAt(p.x, p.y, p.z)
                if (c != null)
                    c.notify("A %s lands between your feet.", item.name)
                return
            } else {
                val neighbors = p.neighbors8()
                neighbors.removeAll(checked)
                points.addAll(neighbors)
            }
        }
    }
}

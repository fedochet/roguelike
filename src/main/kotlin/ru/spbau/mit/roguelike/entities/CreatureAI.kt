package ru.spbau.mit.roguelike.entities

import ru.spbau.mit.roguelike.Tile
import ru.spbau.mit.roguelike.world.FieldOfView
import ru.spbau.mit.roguelike.world.Line
import ru.spbau.mit.roguelike.world.MessagesHub

open class CreatureAI(protected var creature: Creature) {

    init {
        this.creature.ai = this
    }

    open fun onEnter(x: Int, y: Int, z: Int, tile: Tile) {
        if (tile.steppable) {
            creature.x = x
            creature.y = y
            creature.z = z
        } else {
            creature.doAction("bump into a wall")
        }
    }

    open fun onUpdate() {}

    open fun onNotify(message: String) {}

    open fun canSee(wx: Int, wy: Int, wz: Int): Boolean {
        if (creature.z != wz)
            return false

        if ((creature.x - wx) * (creature.x - wx) + (creature.y - wy) * (creature.y - wy) > creature.visionRadius * creature.visionRadius)
            return false

        for (p in Line.create(creature.x, creature.y, wx, wy)) {
            if (creature.tile(p.x, p.y, wz).steppable || p.x == wx && p.y == wy)
                continue

            return false
        }

        return true
    }

    open fun wander() {
        val mx = (Math.random() * 3).toInt() - 1
        val my = (Math.random() * 3).toInt() - 1

        val other = creature.creature(creature.x + mx, creature.y + my, creature.z)

        if (other != null && other.name == creature.name)
            return
        else
            creature.moveBy(mx, my, 0)
    }
}

open class DummyAI(creature: Creature) : CreatureAI(creature) {
    override fun onEnter(x: Int, y: Int, z: Int, tile: Tile) {}
}

class PlayerAI(
        player: Creature,
        private val fieldOfView: FieldOfView,
        private val messages: MessagesHub): CreatureAI(player) {

    init {
        player.ai = this
    }

    override fun onEnter(x: Int, y: Int, z: Int, tile: Tile) {
        when {
            tile.steppable -> {
                creature.x = x
                creature.y = y
                creature.z = z
            }

            tile.diggable -> creature.dig(x, y, z)

            else -> creature.doAction("bumb into wall")
        }
    }

    override fun onNotify(message: String) {
        messages.add(message)
    }

    override fun canSee(wx: Int, wy: Int, wz: Int) = fieldOfView.isVisible(wx, wy, wz)
}

class FungusAI(fungus: Creature): DummyAI(fungus)

class BatAI(bat: Creature): CreatureAI(bat) {
    override fun onUpdate() {
        wander()
        wander()
    }
}

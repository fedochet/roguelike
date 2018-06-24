package ru.spbau.mit.roguelike.entities

import ru.spbau.mit.roguelike.world.Tile
import ru.spbau.mit.roguelike.world.FieldOfView
import ru.spbau.mit.roguelike.world.Line
import ru.spbau.mit.roguelike.world.MessagesHub

/**
 * Abstract class that represents controls over passed [creature]'s behaviour.
 *
 * Sets [creature]'s ai to itself in constructor.
 */
open class CreatureAI(protected var creature: Creature) {

    init {
        this.creature.ai = this
    }

    /**
     * Action to perform when creature is about to enter some location.
     */
    open fun onEnter(x: Int, y: Int, z: Int, tile: Tile) {
        if (tile.steppable) {
            creature.x = x
            creature.y = y
            creature.z = z
        } else {
            creature.doAction("bump into a wall")
        }
    }

    /**
     * Action to perform on every step of game.
     */
    open fun onUpdate() {}

    /**
     * Action to do when somebody notifies this ai with message.
     */
    open fun onNotify(message: String) {}

    /**
     * Method to check if this ai can see specific location.
     */
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
}

/**
 * AI that doesn't do anything particularly smart.
 */
open class DummyAI(creature: Creature) : CreatureAI(creature) {
    override fun onEnter(x: Int, y: Int, z: Int, tile: Tile) {}
}

/**
 * Main AI that helps to communicate to user with [MessagesHub] and
 * also uses [FieldOfView] instance to provide more realistic world view.
 */
class PlayerAI(
        player: Creature,
        private val fieldOfView: FieldOfView,
        private val messages: MessagesHub): CreatureAI(player) {

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

/**
 * AI that does nothing, perfectly fine for fungus.
 */
class FungusAI(fungus: Creature): DummyAI(fungus)

/**
 * Bat AI, on every step it randomly flies around.
 *
 * Does not attack creatures with the same name.
 */
class BatAI(bat: Creature): CreatureAI(bat) {
    override fun onUpdate() {
        wander()
        wander()
    }

    private fun wander() {
        val mx = (Math.random() * 3).toInt() - 1
        val my = (Math.random() * 3).toInt() - 1

        val other = creature.creature(creature.x + mx, creature.y + my, creature.z)

        if (other != null && other.name == creature.name)
            return
        else
            creature.moveBy(mx, my, 0)
    }
}

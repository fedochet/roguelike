package ru.spbau.mit.roguelike.entities

import ru.spbau.mit.roguelike.Tile
import ru.spbau.mit.roguelike.World
import ru.spbau.mit.roguelike.util.roundAreaCoordinates
import java.awt.Color

abstract class CreatureAI(protected val creature: Creature) {
    abstract fun onEnter(x: Int, y: Int, tile: Tile)
    open fun onNotify(message: String) {}
}

open class DummyAI(creature: Creature) : CreatureAI(creature) {
    override fun onEnter(x: Int, y: Int, tile: Tile) {}
}

class MessagesHub {
    val messages: List<String> get() = _messages
    private val _messages: MutableList<String> = mutableListOf()

    fun add(message: String) = _messages.add(message)
    fun clear() = _messages.clear()
}

class PlayerAI(player: Creature, private val messages: MessagesHub): CreatureAI(player) {
    init {
        player.ai = this
    }

    override fun onEnter(x: Int, y: Int, tile: Tile) {
        when {
            tile.steppable -> {
                creature.x = x
                creature.y = y
            }

            tile.diggable -> creature.dig(x, y)
        }
    }

    override fun onNotify(message: String) {
        messages.add(message)
    }
}

class FungusAI(fungus: Creature): DummyAI(fungus)

class Creature(
        private val world: World,
        val glyph: Char,
        val color: Color,
        val maxHp: Int,
        private val attackValue: Int,
        private val defenseValue: Int,
        var x: Int = 0,
        var y: Int = 0) {

    var hp = maxHp


    var ai: CreatureAI = DummyAI(this)

    fun moveBy(mx: Int, my: Int) {
        val nextX = x + mx
        val nextY = y + my

        val other = world.getCreatureAt(nextX, nextY)

        if (other != null) {
            attack(other)
        } else {
            ai.onEnter(nextX, nextY, world.getTile(nextX, nextY))
        }
    }

    fun attack(other: Creature) {
        var amount = Math.max(0, attackValue - other.defenseValue)
        amount = (Math.random() * amount).toInt() + 1
        other.modifyHp(-amount)

        doAction("attack the '%s' for %d damage", other.glyph, amount)
    }

    fun modifyHp(amount: Int) {
        hp += amount

        if (hp <= 0) {
            doAction("die")
            world.removeCreature(this)
        }
    }

    fun notify(message: String, vararg params: Any) {
        ai.onNotify(message.format(*params))
    }

    fun doAction(message: String, vararg params: Any) {
        for ((ox, oy) in roundAreaCoordinates(radius = 9)) {
            val other = world.getCreatureAt(x + ox, y + oy) ?: continue

            when (other) {
                this -> other.notify("You $message.", *params)
                else -> other.notify("The '$glyph' $message.", *params)
            }
        }
    }

    fun dig(x: Int, y: Int) = world.dig(x, y)
}

class CreatureFactory(private val world: World) {

    fun newPlayer(messages: MessagesHub): Creature {
        val player = Creature(world, '@', Color.WHITE, 100, 20, 5)
        world.addToEmptyLocation(player)
        PlayerAI(player, messages)
        return player
    }

    fun newFungus(): Creature {
        val fungus = Creature(world, 'f', Color.GREEN, 10, 0, 0)
        world.addToEmptyLocation(fungus)
        FungusAI(fungus)
        return fungus
    }
}
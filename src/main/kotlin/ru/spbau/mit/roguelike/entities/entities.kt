package ru.spbau.mit.roguelike.entities

import ru.spbau.mit.roguelike.Tile
import ru.spbau.mit.roguelike.util.roundAreaCoordinates
import ru.spbau.mit.roguelike.world.Line
import ru.spbau.mit.roguelike.world.World
import java.awt.Color


abstract class CreatureAI(protected val creature: Creature) {
    abstract fun onEnter(x: Int, y: Int, z: Int, tile: Tile)
    open fun onNotify(message: String) {}
    open fun canSee(wx: Int, wy: Int, wz: Int): Boolean = false
}

open class DummyAI(creature: Creature) : CreatureAI(creature) {
    override fun onEnter(x: Int, y: Int, z: Int, tile: Tile) {}
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

    override fun onEnter(x: Int, y: Int, z: Int, tile: Tile) {
        when {
            tile.steppable -> {
                creature.x = x
                creature.y = y
                creature.z = z
            }

            tile.diggable -> creature.dig(x, y, z)
        }
    }

    override fun onNotify(message: String) {
        messages.add(message)
    }

    override fun canSee(wx: Int, wy: Int, wz: Int): Boolean {
        if (creature.z != wz)
            return false

        val distanceToCoordinate = (creature.x - wx) * (creature.x - wx) + (creature.y - wy) * (creature.y - wy)
        if (distanceToCoordinate > creature.visionRadius * creature.visionRadius)
            return false

        for (p in Line.create(creature.x, creature.y, wx, wy)) {
            if (creature.tile(p.x, p.y, wz).steppable || (p.x == wx && p.y == wy))
                continue

            return false
        }

        return true
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
        val visionRadius: Int,
        var x: Int = 0,
        var y: Int = 0,
        var z: Int = 0) {

    var hp = maxHp
    var ai: CreatureAI = DummyAI(this)

    fun canSee(wx: Int, wy: Int, wz: Int): Boolean {
        return ai.canSee(wx, wy, wz)
    }

    fun tile(wx: Int, wy: Int, wz: Int): Tile {
        return world.getTile(wx, wy, wz)
    }


    fun moveBy(mx: Int, my: Int, mz: Int) {
        val nextX = x + mx
        val nextY = y + my
        val nextZ = z + mz

        val tile = world.getTile(nextX, nextY, nextZ)

        if (mz == -1) {
            if (tile == Tile.STAIRS_DOWN) {
                doAction("walk up the stairs to level %d", z + mz + 1)
            } else {
                doAction("try to go up but are stopped by the cave ceiling")
                return
            }
        } else if (mz == 1) {
            if (tile == Tile.STAIRS_UP) {
                doAction("walk down the stairs to level %d", z + mz + 1)
            } else {
                doAction("try to go down but are stopped by the cave floor")
                return
            }
        }

        val other = world.getCreatureAt(nextX, nextY, nextZ)

        if (other != null) {
            attack(other)
        } else {
            ai.onEnter(nextX, nextY, nextZ, tile)
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
            val other = world.getCreatureAt(x + ox, y + oy, z) ?: continue

            when (other) {
                this -> other.notify("You $message.", *params)
                else -> other.notify("The '$glyph' $message.", *params)
            }
        }
    }

    fun dig(x: Int, y: Int, z: Int) = world.dig(x, y, z)
}

class CreatureFactory(private val world: World) {

    fun newPlayer(messages: MessagesHub): Creature {
        val player = Creature(world, '@', Color.WHITE, 100, 20, 5, 9)
        world.addToEmptyLocation(player)
        PlayerAI(player, messages)
        return player
    }

    fun newFungus(): Creature {
        val fungus = Creature(world, 'f', Color.GREEN, 10, 0, 0, 0)
        world.addToEmptyLocation(fungus)
        FungusAI(fungus)
        return fungus
    }
}
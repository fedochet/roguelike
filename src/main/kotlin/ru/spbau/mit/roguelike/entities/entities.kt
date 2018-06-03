package ru.spbau.mit.roguelike.entities

import ru.spbau.mit.roguelike.Tile
import ru.spbau.mit.roguelike.util.roundAreaCoordinates
import ru.spbau.mit.roguelike.world.FieldOfView
import ru.spbau.mit.roguelike.world.Line
import ru.spbau.mit.roguelike.world.World
import java.awt.Color


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

class MessagesHub {
    val messages: List<String> get() = _messages
    private val _messages: MutableList<String> = mutableListOf()

    fun add(message: String) = _messages.add(message)
    fun clear() = _messages.clear()
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

class Creature(
        private val world: World,
        val name: String,
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

        doAction("attack the '%s' for %d damage", other.name, amount)
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

    fun update() {
        ai.onUpdate()
    }

    fun doAction(message: String, vararg params: Any) {
        for ((ox, oy) in roundAreaCoordinates(radius = 9)) {
            val other = world.getCreatureAt(x + ox, y + oy, z) ?: continue

            when {
                other == this -> other.notify("You $message.", *params)
                other.canSee(x, y, z) -> other.notify("The '$name' $message.", *params)
            }
        }
    }

    fun dig(x: Int, y: Int, z: Int) = world.dig(x, y, z)

    fun tile(x: Int, y: Int, z: Int) = world.getTile(x, y, z)
    fun creature(x: Int, y: Int, z: Int): Creature? = world.getCreatureAt(x, y, z)
}

class CreatureFactory(private val world: World) {

    fun newPlayer(fieldOfView: FieldOfView, messages: MessagesHub): Creature {
        val player = Creature(world, "Player", '@', Color.WHITE, 100, 20, 5, 9)
        world.addToEmptyLocation(player)
        PlayerAI(player, fieldOfView, messages)
        return player
    }

    fun newFungus(): Creature {
        val fungus = Creature(world, "Fungus", 'f', Color.GREEN, 10, 0, 0, 0)
        world.addToEmptyLocation(fungus)
        FungusAI(fungus)
        return fungus
    }

    fun newBat(): Creature {
        val bat = Creature(world, "Bat", 'b', Color.YELLOW, 15, 5, 0, 0)
        world.addToEmptyLocation(bat)
        BatAI(bat)
        return bat
    }
}
package ru.spbau.mit.roguelike.entities

import ru.spbau.mit.roguelike.Tile
import ru.spbau.mit.roguelike.util.roundAreaCoordinates
import ru.spbau.mit.roguelike.world.FieldOfView
import ru.spbau.mit.roguelike.world.Item
import ru.spbau.mit.roguelike.world.MessagesHub
import ru.spbau.mit.roguelike.world.World
import java.awt.Color
import kotlin.math.min


class Creature(
        private val world: World,
        val name: String,
        val glyph: Char,
        val color: Color,
        val maxHp: Int,
        private val initialAttackValue: Int,
        private val initialDefenceValue: Int,
        val visionRadius: Int) {

    var x: Int = 0
    var y: Int = 0
    var z: Int = 0


    private var weapon: Item? = null
    private var armor: Item? = null

    private val attackValue get() = initialAttackValue + (weapon?.attackValue ?: 0)
    private val defenseValue get() = initialDefenceValue + (weapon?.defenseValue ?: 0)

    var hp = maxHp
        set(value) {
            field = min(value, maxHp)
            if (field <= 0) {
                die()
            }
        }

    var ai: CreatureAI = DummyAI(this)
    val inventory = Inventory(20)

    fun canSee(wx: Int, wy: Int, wz: Int): Boolean {
        return ai.canSee(wx, wy, wz)
    }

    fun unequip(item: Item?) {
        val actualItem = item ?: return

        if (actualItem === armor) {
            doAction("remove a " + actualItem.name)
            armor = null
        } else if (actualItem === weapon) {
            doAction("put away a " + actualItem.name)
            weapon = null
        }
    }

    fun equip(item: Item) {
        if (item.attackValue == 0 && item.defenseValue == 0)
            return

        if (item.attackValue >= item.defenseValue) {
            unequip(weapon)
            doAction("wield a " + item.name)
            weapon = item
        } else {
            unequip(armor)
            doAction("put on a " + item.name)
            armor = item
        }
    }

    fun eat(item: Item) {
        if (item.foodValue <= 0)
            notify("Cannot eat that!")

        val previousHp = hp
        hp += item.foodValue

        doAction("eat ${item.name}, recover ${hp - previousHp} health")

        inventory.remove(item)
        unequip(item)
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

        other.hp -= amount

        doAction("attack the '%s' for %d damage", other.name, amount)
    }

    fun pickup() {
        val item = world.getItem(x, y, z)

        if (inventory.isFull() || item == null) {
            doAction("grab at the ground")
        } else {
            doAction("pickup a %s", item.name)
            world.removeItem(x, y, z)
            inventory.add(item)
        }
    }

    fun drop(item: Item) {
        doAction("drop a " + item.name)
        unequip(item)
        inventory.remove(item)
        world.addAtEmptySpace(item, x, y, z)
    }

    private fun die() {
        doAction("die")
        world.removeCreature(this)
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
        val player = Creature(
                world, "Player", '@', Color.WHITE,
                maxHp = 10,
                initialAttackValue = 20,
                initialDefenceValue = 5,
                visionRadius = 9
        )
        world.addAtEmptyLocation(player, 0)
        PlayerAI(player, fieldOfView, messages)
        return player
    }

    fun newFungus(level: Int): Creature {
        val fungus = Creature(world, "Fungus", 'f', Color.GREEN, 10, 0, 0, 0)
        world.addAtEmptyLocation(fungus, level)
        FungusAI(fungus)
        return fungus
    }

    fun newBat(level: Int): Creature {
        val bat = Creature(world, "Bat", 'b', Color.YELLOW, 15, 5, 0, 0)
        world.addAtEmptyLocation(bat, level)
        BatAI(bat)
        return bat
    }
}
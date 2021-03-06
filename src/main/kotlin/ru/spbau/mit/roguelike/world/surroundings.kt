package ru.spbau.mit.roguelike.world


import java.awt.*
import asciiPanel.AsciiPanel

/**
 * Data class that represents item in game world.
 *
 * Item can be used by [ru.spbau.mit.roguelike.entities.Creature] and can increase its attack or defence values,
 * or can be eaten and heal it.
 */
data class Item(val name: String, val glyph: Char, val color: Color) {
    var attackValue: Int = 0
    var defenseValue: Int = 0
    var foodValue: Int = 0
}

/**
 * Special unique item that should be collected in order to win the game.
 */
val ZACHETKA = Item("Zachetka", '*', Color.CYAN)

/**
 * Factory class that can create random items (like sword, potions etc.) in [world] and place it at random places.
 */
class StuffFactory(private val world: World) {
    fun newRock(level: Int): Item {
        val item = Item("Rock", '.', Color.YELLOW)
        world.addItemAtEmptyLocation(item, level)
        return item
    }

    fun newVictoryItem(depth: Int): Item {
        world.addItemAtEmptyLocation(ZACHETKA, depth)
        return ZACHETKA
    }

    fun newDagger(depth: Int): Item {
        val item = Item("dagger", ')', AsciiPanel.white)
        item.attackValue += 5
        world.addItemAtEmptyLocation(item, depth)
        return item
    }

    fun newSword(depth: Int): Item {
        val item = Item("sword", ')', AsciiPanel.brightWhite)
        item.attackValue += 10
        world.addItemAtEmptyLocation(item, depth)
        return item
    }

    fun newStaff(depth: Int): Item {
        val item = Item("staff", ')', AsciiPanel.yellow)
        item.attackValue += 5
        item.defenseValue += 3
        world.addItemAtEmptyLocation(item, depth)
        return item
    }

    fun newLightArmor(depth: Int): Item {
        val item = Item("tunic", '[', AsciiPanel.green)
        item.defenseValue += (2)
        world.addItemAtEmptyLocation(item, depth)
        return item
    }

    fun newMediumArmor(depth: Int): Item {
        val item = Item("chainmail", '[', AsciiPanel.white)
        item.defenseValue += 4
        world.addItemAtEmptyLocation(item, depth)
        return item
    }

    fun newHeavyArmor(depth: Int): Item {
        val item = Item("platemail", '[', AsciiPanel.brightWhite)
        item.defenseValue += 6
        world.addItemAtEmptyLocation(item, depth)
        return item
    }

    fun randomWeapon(depth: Int): Item {
        return when ((Math.random() * 3).toInt()) {
            0 -> newDagger(depth)
            1 -> newSword(depth)
            else -> newStaff(depth)
        }
    }

    fun newPotion(depth: Int): Item {
        val potion = Item("health potion", '&', AsciiPanel.brightRed)
        potion.foodValue += 10
        world.addItemAtEmptyLocation(potion, depth)
        return potion
    }

    fun randomArmor(depth: Int): Item {
        return when ((Math.random() * 3).toInt()) {
            0 -> newLightArmor(depth)
            1 -> newMediumArmor(depth)
            else -> newHeavyArmor(depth)
        }
    }
}

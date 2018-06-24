package ru.spbau.mit.roguelike.screens

import ru.spbau.mit.roguelike.entities.Creature
import ru.spbau.mit.roguelike.world.Item

class EquipScreen(player: Creature) : InventoryBasedScreen(player) {

    override val verb: String
        get() = "wear or wield"

    override fun isAcceptable(item: Item): Boolean {
        return item.attackValue > 0 || item.defenseValue > 0
    }

    override fun use(item: Item): Screen? {
        player.equip(item)
        return null
    }
}

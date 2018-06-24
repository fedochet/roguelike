package ru.spbau.mit.roguelike.screens

import ru.spbau.mit.roguelike.entities.Creature
import ru.spbau.mit.roguelike.world.Item

class DropScreen(player: Creature) : InventoryBasedScreen(player) {

    override fun isAcceptable(item: Item): Boolean {
        return true
    }

    override val verb = "drop"
    override fun use(item: Item): Screen? {
        player.drop(item)
        return null
    }
}

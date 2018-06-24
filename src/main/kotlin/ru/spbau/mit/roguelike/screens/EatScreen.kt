package ru.spbau.mit.roguelike.screens

import ru.spbau.mit.roguelike.entities.Creature
import ru.spbau.mit.roguelike.world.Item

class EatScreen(player: Creature): InventoryBasedScreen(player) {
    override val verb = "eat"

    override fun isAcceptable(item: Item): Boolean = item.foodValue > 0

    override fun use(item: Item): Screen? {
        player.eat(item)
        return null
    }
}
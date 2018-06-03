package ru.spbau.mit.roguelike.world


import java.awt.*

data class Item(val name: String, val glyph: Char, val color: Color)

class StuffFactory(private val world: World) {
    fun newRock(level: Int): Item {
        val item = Item("Rock", '.', Color.YELLOW)
        world.addToEmptyLocation(item, level)
        return item
    }
}

package ru.spbau.mit.roguelike.screens


import asciiPanel.AsciiPanel
import ru.spbau.mit.roguelike.entities.Creature
import ru.spbau.mit.roguelike.world.Item

import java.awt.event.KeyEvent
import java.util.ArrayList

abstract class InventoryBasedScreen(protected var player: Creature) : Screen {
    private val letters: String

    protected abstract val verb: String

    private val list: ArrayList<String>
        get() {
            val lines = ArrayList<String>()
            val inventory = player.inventory.items

            for (i in inventory.indices) {
                val item = inventory[i]

                if (item == null || !isAcceptable(item))
                    continue

                val line = letters[i] + " - " + item.glyph + " " + item.name

                lines.add(line)
            }
            return lines
        }

    protected abstract fun isAcceptable(item: Item): Boolean
    protected abstract fun use(item: Item): Screen?

    init {
        this.letters = "abcdefghijklmnopqrstuvwxyz"
    }

    override fun displayOutput(terminal: AsciiPanel) {
        val lines = list

        var y = 23 - lines.size
        val x = 4

        if (lines.size > 0)
            terminal.clear(' ', x, y, 20, lines.size)

        for (line in lines) {
            terminal.write(line, x, y++)
        }

        terminal.clear(' ', 0, 23, 80, 1)
        terminal.write("What would you like to $verb?", 2, 23)

        terminal.repaint()
    }

    override fun respondToUserInput(key: KeyEvent): Screen? {
        val c = key.keyChar

        val items = player.inventory.items

        val targetItem = items.getOrNull(letters.indexOf(c))
        if (targetItem != null && isAcceptable(targetItem)) {
            return use(targetItem)
        }

        return if (key.keyCode == KeyEvent.VK_ESCAPE) null else this
    }

}


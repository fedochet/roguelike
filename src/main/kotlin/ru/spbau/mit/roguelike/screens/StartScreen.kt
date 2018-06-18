package ru.spbau.mit.roguelike.screens

import asciiPanel.AsciiPanel
import java.awt.event.KeyEvent


/**
 * This class represents start screen with some foreword to player. From here player is able to proceed to [PlayScreen].
 */
class StartScreen : Screen {
    override fun displayOutput(terminal: AsciiPanel) {
        terminal.write("Welcome to roguelike!", 1, 1)
        terminal.writeText("In order to win, you will have to travel to the lowest level of dunegon and get Zachetka", 1, 2)
        terminal.writeText("To move, use arrows; to grub item, use `,` or `g`.", 1, 5)
        terminal.writeText("To open inventory, use `E` (eating), `D` (dropping) and `W` (wearing).", 1, 6)
        terminal.writeCenter("-- press [enter] to start --", 22)

        println(terminal.widthInCharacters)
    }

    override fun respondToUserInput(key: KeyEvent): Screen {
        return if (key.keyCode == KeyEvent.VK_ENTER) PlayScreen() else this
    }
}

private fun AsciiPanel.writeText(text: String, x: Int, y: Int) {

    text.chunked(this.widthInCharacters - x)
            .map { it.trim() }
            .forEachIndexed {i, msg -> this.write(msg, x, y + i) }
}
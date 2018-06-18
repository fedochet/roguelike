package ru.spbau.mit.roguelike.screens

import asciiPanel.AsciiPanel
import java.awt.event.KeyEvent

/**
 * This class represents victory screen that is reached after you complete the game.
 */
class WinScreen : Screen {

    override fun displayOutput(terminal: AsciiPanel) {
        terminal.write("You won.", 1, 1)
        terminal.writeCenter("-- press [enter] to restart --", 22)
    }

    override fun respondToUserInput(key: KeyEvent): Screen {
        return if (key.keyCode == KeyEvent.VK_ENTER) PlayScreen() else this
    }
}
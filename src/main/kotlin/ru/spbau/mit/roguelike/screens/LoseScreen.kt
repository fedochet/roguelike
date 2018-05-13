package ru.spbau.mit.roguelike.screens

import java.awt.event.KeyEvent
import asciiPanel.AsciiPanel

class LoseScreen : Screen {

    override fun displayOutput(terminal: AsciiPanel) {
        terminal.write("You lost.", 1, 1)
        terminal.writeCenter("-- press [enter] to restart --", 22)
    }

    override fun respondToUserInput(key: KeyEvent): Screen {
        return if (key.keyCode == KeyEvent.VK_ENTER) PlayScreen() else this
    }
}
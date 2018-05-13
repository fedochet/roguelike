package ru.spbau.mit.roguelike.screens

import asciiPanel.AsciiPanel
import java.awt.event.KeyEvent


class StartScreen : Screen {
    override fun displayOutput(terminal: AsciiPanel) {
        terminal.write("rl tutorial", 1, 1)
        terminal.writeCenter("-- press [enter] to start --", 22)
    }

    override fun respondToUserInput(key: KeyEvent): Screen {
        return if (key.keyCode == KeyEvent.VK_ENTER) PlayScreen() else this
    }
}
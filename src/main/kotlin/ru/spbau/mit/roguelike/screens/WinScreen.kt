package ru.spbau.mit.roguelike.screens

import javafx.scene.input.KeyCode.getKeyCode
import asciiPanel.AsciiPanel
import java.awt.event.KeyEvent


class WinScreen : Screen {

    override fun displayOutput(terminal: AsciiPanel) {
        terminal.write("You won.", 1, 1)
        terminal.writeCenter("-- press [enter] to restart --", 22)
    }

    override fun respondToUserInput(key: KeyEvent): Screen {
        return if (key.keyCode == KeyEvent.VK_ENTER) PlayScreen() else this
    }
}
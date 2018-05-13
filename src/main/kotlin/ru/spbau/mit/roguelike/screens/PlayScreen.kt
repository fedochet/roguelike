package ru.spbau.mit.roguelike.screens

import asciiPanel.AsciiPanel
import java.awt.event.KeyEvent


class PlayScreen : Screen {

    override fun displayOutput(terminal: AsciiPanel) {
        terminal.write("You are having fun.", 1, 1)
        terminal.writeCenter("-- press [escape] to lose or [enter] to win --", 22)
    }

    override fun respondToUserInput(key: KeyEvent): Screen {
        when (key.getKeyCode()) {
            KeyEvent.VK_ESCAPE -> return LoseScreen()
            KeyEvent.VK_ENTER -> return WinScreen()
        }

        return this
    }
}
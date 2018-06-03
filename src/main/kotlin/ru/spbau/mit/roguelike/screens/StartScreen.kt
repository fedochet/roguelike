package ru.spbau.mit.roguelike.screens

import asciiPanel.AsciiPanel
import java.awt.event.KeyEvent


class StartScreen : Screen {
    override fun displayOutput(terminal: AsciiPanel) {
        terminal.write("Welcome to roguelike!", 1, 1)
        terminal.write("In order to win, you will have to travel to the lowest", 1, 2)
        terminal.write("level of dunegon and get Zachetka", 1, 3)
        terminal.writeCenter("-- press [enter] to start --", 22)
    }

    override fun respondToUserInput(key: KeyEvent): Screen {
        return if (key.keyCode == KeyEvent.VK_ENTER) PlayScreen() else this
    }
}
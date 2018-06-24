package ru.spbau.mit.roguelike.screens

import asciiPanel.AsciiPanel
import java.awt.event.KeyEvent

/**
 * Represents screen object that can draw itself and can respond to user input.
 */
interface Screen {
    fun displayOutput(terminal: AsciiPanel)
    fun respondToUserInput(key: KeyEvent): Screen?
}
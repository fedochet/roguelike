package ru.spbau.mit.roguelike.screens

import asciiPanel.AsciiPanel
import java.awt.event.KeyEvent

interface Screen {
    fun displayOutput(terminal: AsciiPanel)
    fun respondToUserInput(key: KeyEvent): Screen?
}
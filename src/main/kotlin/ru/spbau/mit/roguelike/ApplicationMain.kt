package ru.spbau.mit.roguelike

import asciiPanel.AsciiPanel
import ru.spbau.mit.roguelike.screens.Screen
import ru.spbau.mit.roguelike.screens.StartScreen
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame

class ApplicationMain : JFrame(), KeyListener {

    private val terminal: AsciiPanel = AsciiPanel()
    private var screen: Screen

    init {
        add(terminal)
        pack()
        screen = StartScreen()
        addKeyListener(this)
        repaint()
    }

    override fun repaint() {
        terminal.clear()
        screen.displayOutput(terminal)
        super.repaint()
    }

    override fun keyPressed(e: KeyEvent) {
        screen = screen.respondToUserInput(e)
        repaint()
    }

    override fun keyReleased(e: KeyEvent) {}

    override fun keyTyped(e: KeyEvent) {}
}

fun main(args: Array<String>) {
    val app = ApplicationMain()
    app.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    app.isVisible = true
}

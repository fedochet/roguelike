package ru.spbau.mit.roguelike.screens

import asciiPanel.AsciiPanel
import ru.spbau.mit.roguelike.World
import ru.spbau.mit.roguelike.WorldBuilder
import java.awt.event.KeyEvent
import java.lang.Math.*

class PlayScreen : Screen {

    private var centerX: Int = 0
    private var centerY: Int = 0
    private val screenWidth: Int = 80
    private val screenHeight: Int = 21

    private val world: World = WorldBuilder(90, 30).makeCaves().build()

    override fun displayOutput(terminal: AsciiPanel) {
        val left = getScrollX()
        val top = getScrollY()

        displayTiles(terminal, left, top)
        terminal.write('X', centerX - left, centerY - top);
    }

    override fun respondToUserInput(key: KeyEvent): Screen {
        when (key.keyCode) {
            KeyEvent.VK_RIGHT -> scrollBy(1, 0)
            KeyEvent.VK_LEFT -> scrollBy(-1, 0)
            KeyEvent.VK_UP -> scrollBy(0, -1)
            KeyEvent.VK_DOWN -> scrollBy(0, 1)
        }

        return when (key.keyCode) {
            KeyEvent.VK_ESCAPE -> LoseScreen()
            KeyEvent.VK_ENTER -> WinScreen()
            else -> this
        }
    }


    private fun getScrollX(): Int {
        return (centerX - screenWidth / 2).keepInRange(0, world.width - screenWidth)
    }

    private fun getScrollY(): Int {
        return (centerY - screenHeight / 2).keepInRange(0, world.height - screenHeight)
    }

    private fun displayTiles(terminal: AsciiPanel, left: Int, top: Int) {
        for (x in 0 until screenWidth) {
            for (y in 0 until screenHeight) {
                val wx = x + left
                val wy = y + top

                terminal.write(world.getGlyph(wx, wy), x, y, world.getColor(wx, wy))
            }
        }
    }

    private fun scrollBy(mx: Int, my: Int) {
        centerX = (centerX + mx).keepInRange(0, world.width - 1)
        centerY = (centerY + my).keepInRange(0, world.height - 1)
    }

}

/**
 * @return [this] if it is in range `[from, to]`, or closest to it value from range otherwise.
 */
private fun Int.keepInRange(from: Int, to: Int): Int {
    return max(from, min(this, to))
}
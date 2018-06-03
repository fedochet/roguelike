package ru.spbau.mit.roguelike.screens

import asciiPanel.AsciiPanel
import ru.spbau.mit.roguelike.Tile
import ru.spbau.mit.roguelike.entities.Creature
import ru.spbau.mit.roguelike.entities.CreatureFactory
import ru.spbau.mit.roguelike.util.product
import ru.spbau.mit.roguelike.world.*
import java.awt.Color
import java.awt.event.KeyEvent
import java.lang.Math.max
import java.lang.Math.min


class PlayScreen : Screen {

    private var subscreen: Screen? = null

    private val screenWidth: Int = 80
    private val screenHeight: Int = 21

    private val screenCoordinates = (0 until screenWidth) product (0 until screenHeight)

    private val world: World = WorldBuilder(90, 30, 5).makeCaves().build()
    private val fieldOfView = FieldOfView(world)
    private val messagesHub = MessagesHub()
    private val player: Creature

    init {
        val factory = CreatureFactory(world)
        player = factory.newPlayer(fieldOfView, messagesHub)
        addEnemies(factory)
        addItems(StuffFactory(world))
    }

    private fun addItems(factory: StuffFactory) {
        repeat(world.depth) { z ->
            repeat((world.width * world.height) / 20) {
                factory.newRock(z)
            }
        }

        factory.newVictoryItem(world.depth - 1);
    }

    private fun addEnemies(factory: CreatureFactory) {
        repeat(world.depth) { z ->
            repeat(7) {
                factory.newFungus(z)
            }
            repeat(40) {
                factory.newBat(z)
            }
        }
    }

    override fun displayOutput(terminal: AsciiPanel) {
        subscreen?.let {
            it.displayOutput(terminal)
            return
        }

        val left = getScrollX()
        val top = getScrollY()

        displayTiles(terminal, left, top)
        displayPlayerStats(terminal)
        displayMessages(terminal)

        terminal.drawCreature(player, left, top)
    }

    private fun displayMessages(terminal: AsciiPanel) {
        val top = screenHeight - messagesHub.size + 1
        messagesHub.forEachIndexed { i, message ->
            terminal.writeCenter(message, top - i)
        }

        messagesHub.clear()
    }


    private fun displayPlayerStats(terminal: AsciiPanel) {
        val stats = String.format(" %3d/%3d hp", player.hp, player.maxHp)
        terminal.write(stats, 1, 23)
    }

    private fun AsciiPanel.drawCreature(creature: Creature, left: Int, top: Int) {
        val onScreenX = creature.x - left
        val onScreenY = creature.y - top

        if (onScreenCoordinates(onScreenX, onScreenY)) {
            write(creature.glyph, onScreenX, onScreenY, creature.color)
        }
    }

    override fun respondToUserInput(key: KeyEvent): Screen {
        subscreen?.let {
            subscreen = it.respondToUserInput(key)
            return this
        }

        when (key.keyCode) {
            KeyEvent.VK_RIGHT -> scrollBy(1, 0)
            KeyEvent.VK_LEFT -> scrollBy(-1, 0)
            KeyEvent.VK_UP -> scrollBy(0, -1)
            KeyEvent.VK_DOWN -> scrollBy(0, 1)
            KeyEvent.VK_D -> subscreen = DropScreen(player)
        }

        when (key.keyChar) {
            ',', 'g' -> player.pickup()

            '<' -> {
                if (userIsTryingToExit())
                    return userExits();

                player.moveBy(0, 0, -1);
            }

            '>' -> player.moveBy(0, 0, 1)
        }

        world.update()

        if (player.hp <= 0) {
            return LoseScreen()
        }

        return this
    }

    private fun userIsTryingToExit(): Boolean {
        return player.z == 0 && world.getTile(player.x, player.y, player.z) === Tile.STAIRS_UP
    }

    private fun userExits(): Screen {
        for (item in player.inventory.items) {
            if (item != null && item.name == "Zachetka")
                return WinScreen()
        }

        return LoseScreen()
    }


    private fun getScrollX(): Int {
        return (player.x - screenWidth / 2).keepInRange(0, world.width - screenWidth)
    }

    private fun getScrollY(): Int {
        return (player.y - screenHeight / 2).keepInRange(0, world.height - screenHeight)
    }

    private fun displayTiles(terminal: AsciiPanel, left: Int, top: Int) {
        fieldOfView.update(player.x, player.y, player.z, player.visionRadius)

        for ((x, y) in screenCoordinates) {
            val wx = x + left
            val wy = y + top

            if (player.canSee(wx, wy, player.z)) {
                terminal.write(world.getGlyph(wx, wy, player.z), x, y, world.getColor(wx, wy, player.z))
            } else {
                terminal.write(fieldOfView.tile(wx, wy, player.z).glyph, x, y, Color.DARK_GRAY)
            }
        }
    }

    private fun onScreenCoordinates(x: Int, y: Int): Boolean {
        return (x in 0 until screenWidth) && (y in 0 until screenHeight)
    }

    private fun scrollBy(mx: Int, my: Int) {
        player.moveBy(mx, my, 0)
    }

}

/**
 * @return [this] if it is in range `[from, to]`, or closest to it value from range otherwise.
 */
private fun Int.keepInRange(from: Int, to: Int): Int {
    return max(from, min(this, to))
}
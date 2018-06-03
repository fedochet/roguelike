package ru.spbau.mit.roguelike

import asciiPanel.AsciiPanel
import java.awt.Color

enum class Tile(val glyph: Char, val color: Color) {
    FLOOR(255.toChar(), AsciiPanel.yellow),
    WALL(177.toChar(), AsciiPanel.yellow),
    BOUNDS('x', AsciiPanel.brightBlack),
    STAIRS_DOWN('>', AsciiPanel.white),
    STAIRS_UP('<', AsciiPanel.white);

    val diggable: Boolean get() = this === WALL
    val steppable: Boolean get() = this !== WALL && this !== BOUNDS
}
package ru.spbau.mit.roguelike

import asciiPanel.AsciiPanel
import java.awt.Color

enum class Tile(val glyph: Char, val color: Color) {
    FLOOR(255.toChar(), AsciiPanel.yellow),
    WALL(177.toChar(), AsciiPanel.yellow),
    BOUNDS('x', AsciiPanel.brightBlack)
}
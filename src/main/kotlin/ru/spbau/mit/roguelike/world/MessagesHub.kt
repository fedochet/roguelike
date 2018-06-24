package ru.spbau.mit.roguelike.world

/**
 * A class to store messages collectively
 * (may be passed to different places, so many can write here and many can read).
 */
class MessagesHub: Iterable<String> {
    override fun iterator() = messages.iterator()

    val size get() = messages.size
    private val messages: MutableList<String> = mutableListOf()

    fun add(message: String) = messages.add(message)
    fun clear() = messages.clear()
}

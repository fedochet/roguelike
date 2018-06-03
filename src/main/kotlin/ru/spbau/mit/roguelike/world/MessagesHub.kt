package ru.spbau.mit.roguelike.world

class MessagesHub: Iterable<String> {
    override fun iterator() = messages.iterator()

    val size get() = messages.size
    private val messages: MutableList<String> = mutableListOf()

    fun add(message: String) = messages.add(message)
    fun clear() = messages.clear()
}

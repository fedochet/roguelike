package ru.spbau.mit.roguelike.entities

import ru.spbau.mit.roguelike.world.Item


/**
 * Class that represents user inventory and may contain [Item]s.
 */
class Inventory(max: Int) {
    val items: Array<Item?> = arrayOfNulls(max)
    operator fun get(i: Int): Item? {
        return items[i]
    }

    fun add(newItem: Item) {
        items.forEachIndexed { i, item ->
            if (item == null) {
                items[i] = newItem
                return
            }
        }
    }


    fun remove(toRemove: Item) {
        items.forEachIndexed {i, item ->
            if (item === toRemove) {
                items[i] = null
                return
            }
        }
    }

    fun isFull() = items.count { it == null} == 0
}

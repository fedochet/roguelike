package ru.spbau.mit.roguelike.util

infix fun <T, K> Iterable<T>.product(other: Iterable<K>) = this.flatMap { i -> other.map { i to it } }
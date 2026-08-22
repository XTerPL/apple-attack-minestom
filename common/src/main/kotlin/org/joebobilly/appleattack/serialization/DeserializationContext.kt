package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.Key

interface DeserializationContext {
    fun <T : Any> readNullable(entry: SerializationEntry<T>): T?
    fun isEmpty(): Boolean
    fun getKeys(): Set<Key>

    companion object {
        fun <T : Any> DeserializationContext.read(entry: SerializationEntry<T>)
                = readNullable(entry) ?: throw NBTReadError(entry.key.toString(), "not found")
        fun <T : Any> DeserializationContext.read(entry: SerializationEntry<T>, default: T)
                = readNullable(entry) ?: default
        fun <T : Any> DeserializationContext.read(entry: SerializationEntry<T>, default: () -> T)
                = readNullable(entry) ?: default()
    }
}
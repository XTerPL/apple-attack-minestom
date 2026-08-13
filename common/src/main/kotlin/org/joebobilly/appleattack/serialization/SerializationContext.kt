package org.joebobilly.appleattack.serialization

import net.minestom.server.tag.TagHandler
import org.bukkit.persistence.PersistentDataContainer

sealed interface SerializationContext {
    fun <T : Any> write(entry: SerializationEntry<T>, value: T)

    class Minestom(private val handler: TagHandler): SerializationContext {
        override fun <T : Any> write(entry: SerializationEntry<T>, value: T) {
            handler.setTag(entry.minestomTag, value)
        }
    }
    class Paper(private val container: PersistentDataContainer): SerializationContext {
        override fun <T : Any> write(entry: SerializationEntry<T>, value: T) {
            entry.persistentDataEntry.set(container, value)
        }
    }
}
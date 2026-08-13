package org.joebobilly.appleattack.serialization

import net.kyori.adventure.nbt.CompoundBinaryTag
import org.bukkit.persistence.PersistentDataContainer

sealed interface DeserializationContext {
    fun <T : Any> readNullable(entry: SerializationEntry<T>): T?
    fun isEmpty(): Boolean

    companion object {
        fun <T : Any> DeserializationContext.read(entry: SerializationEntry<T>)
                = readNullable(entry) ?: throw NBTReadError(entry.key.toString(), "not found")
        fun <T : Any> DeserializationContext.read(entry: SerializationEntry<T>, default: T)
                = readNullable(entry) ?: default
        fun <T : Any> DeserializationContext.read(entry: SerializationEntry<T>, default: () -> T)
                = readNullable(entry) ?: default()
    }

    class Minestom(private val nbt: CompoundBinaryTag) : DeserializationContext {
        override fun <T : Any> readNullable(entry: SerializationEntry<T>): T? {
            return NBTReadError.wrap(entry.key.toString()) {
                entry.minestomTag.read(nbt)
            }
        }
        override fun isEmpty() = nbt.isEmpty
    }
    class Paper(private val container: PersistentDataContainer) : DeserializationContext {
        override fun <T : Any> readNullable(entry: SerializationEntry<T>): T? {
            return NBTReadError.wrap(entry.key.toString()) {
                entry.persistentDataEntry.get(container)
            }
        }
        override fun isEmpty() = container.isEmpty
    }
}
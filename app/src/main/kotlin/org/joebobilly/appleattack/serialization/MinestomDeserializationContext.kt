package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import org.joebobilly.appleattack.serialization.MinestomSerializationEntry.Companion.minestomTag

class MinestomDeserializationContext(private val nbt: CompoundBinaryTag) : DeserializationContext {
    override fun <T : Any> readNullable(entry: SerializationEntry<T>): T? {
        return NBTReadError.wrap(entry.key.toString()) {
            entry.minestomTag.read(nbt)
        }
    }
    override fun isEmpty() = nbt.isEmpty
    override fun getKeys() = nbt.keySet().map { Key.key(it) }.toSet()
}
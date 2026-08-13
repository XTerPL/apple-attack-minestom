package org.joebobilly.appleattack.serialization

import com.google.common.base.Suppliers
import net.kyori.adventure.key.Key
import net.minestom.server.tag.Tag
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toPersistentDataEntry

@Suppress("UNCHECKED_CAST")
data class SerializationEntry<T : Any>(val key: Key, val type: SerializationType<T>) {
    private val _minestomTag = Suppliers.memoize { toMinestomTag() }
    private val _persistentDataEntry = Suppliers.memoize { toPersistentDataEntry() }

    val minestomTag get() = _minestomTag.get() as Tag<T>
    val persistentDataEntry get() = _persistentDataEntry.get() as PersistentDataEntry<*, T>

    private fun toMinestomTag(): Any = type.toMinestomTag(key)
    private fun toPersistentDataEntry(): Any = type.toPersistentDataEntry(key)
}
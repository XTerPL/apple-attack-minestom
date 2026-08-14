package org.joebobilly.appleattack.serialization

import org.bukkit.persistence.PersistentDataContainer
import org.joebobilly.appleattack.serialization.PaperSerializationEntry.Companion.persistentDataEntry

class PaperSerializationContext(private val container: PersistentDataContainer): SerializationContext {
    override fun <T : Any> write(entry: SerializationEntry<T>, value: T) {
        entry.persistentDataEntry.set(container, value)
    }
}
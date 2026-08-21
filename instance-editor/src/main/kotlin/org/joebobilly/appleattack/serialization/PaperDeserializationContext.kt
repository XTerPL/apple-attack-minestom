package org.joebobilly.appleattack.serialization

import io.papermc.paper.persistence.PersistentDataContainerView
import org.joebobilly.appleattack.serialization.PaperSerializationEntry.Companion.persistentDataEntry

class PaperDeserializationContext(private val container: PersistentDataContainerView) : DeserializationContext {
    override fun <T : Any> readNullable(entry: SerializationEntry<T>): T? {
        return NBTReadError.wrap(entry.key.toString()) {
            entry.persistentDataEntry.get(container)
        }
    }
    override fun isEmpty() = container.isEmpty
}
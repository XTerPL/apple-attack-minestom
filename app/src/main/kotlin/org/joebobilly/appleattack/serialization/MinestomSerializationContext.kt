package org.joebobilly.appleattack.serialization

import net.minestom.server.tag.TagHandler
import org.joebobilly.appleattack.serialization.MinestomSerializationEntry.Companion.minestomTag

class MinestomSerializationContext(private val handler: TagHandler) : SerializationContext {
    override fun <T : Any> write(entry: SerializationEntry<T>, value: T) {
        handler.setTag(entry.minestomTag, value)
    }
}
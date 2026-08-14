package org.joebobilly.appleattack

import net.kyori.adventure.key.Key
import org.joebobilly.appleattack.serialization.MinestomSerializationEntry
import org.joebobilly.appleattack.serialization.SerializationType

object MinestomPlatform : Platform {
    override fun <T : Any> buildSerializationEntry(key: Key, type: SerializationType<T>)
        = MinestomSerializationEntry(key, type)
}
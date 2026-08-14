package org.joebobilly.appleattack

import net.kyori.adventure.key.Key
import org.joebobilly.appleattack.serialization.PaperSerializationEntry
import org.joebobilly.appleattack.serialization.SerializationType

object PaperPlatform : Platform {
    override fun <T : Any> buildSerializationEntry(key: Key, type: SerializationType<T>): PaperSerializationEntry<T>
        = PaperSerializationEntry(key, type)
}
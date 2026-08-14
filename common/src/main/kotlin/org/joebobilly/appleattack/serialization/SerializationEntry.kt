package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.Key

interface SerializationEntry<T : Any> {
    val key: Key
    val type: SerializationType<T>
}
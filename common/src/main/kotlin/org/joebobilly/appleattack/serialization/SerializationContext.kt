package org.joebobilly.appleattack.serialization

interface SerializationContext {
    fun <T : Any> write(entry: SerializationEntry<T>, value: T)
}
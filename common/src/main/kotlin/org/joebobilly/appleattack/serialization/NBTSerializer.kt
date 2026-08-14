package org.joebobilly.appleattack.serialization

import kotlin.reflect.KClass

interface NBTSerializer<T : Any> : SerializationType<T> {
    val klass : KClass<T>
    fun read(context: DeserializationContext): T
    fun write(context: SerializationContext, value: T)
}
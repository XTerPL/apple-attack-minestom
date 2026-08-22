package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.Key
import org.joebobilly.appleattack.Platform
import org.joebobilly.appleattack.utils.KeyUtils
import kotlin.reflect.KClass

sealed interface SerializationType<T : Any> {
    data class Mapped<P : Any, C : Any>(
        val primitiveType: SerializationType<P>, val klass: KClass<C>,
        val readMap: (P) -> C, val writeMap: (C) -> P
    ) : SerializationType<C>
    data class ListType<T : Any>(val primitiveType: SerializationType<T>) : SerializationType<List<T>>

    companion object {
        fun <T : Any> SerializationType<T>.toEntry(key: Key) = Platform.instance.buildSerializationEntry(key, this)
        fun <T : Any> SerializationType<T>.toEntry(key: String) = toEntry(KeyUtils.of(key))

        inline fun <P : Any, reified C : Any> SerializationType<P>.map(
            noinline readMap: (P) -> C, noinline writeMap: (C) -> P
        ): SerializationType<C> {
            require(this !is ListType<*>) {
                "Cannot map a list type (thanks paper grrr)"
            }
            return Mapped(this, C::class, readMap, writeMap)
        }
        fun <T : Any> SerializationType<T>.list(): SerializationType<List<T>> = ListType(this)
    }
}
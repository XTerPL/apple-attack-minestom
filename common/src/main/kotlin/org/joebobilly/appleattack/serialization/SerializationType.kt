package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.Key
import net.minestom.server.tag.Tag
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import org.joebobilly.appleattack.utils.KeyUtils

interface SerializationType<T : Any> {
    fun toMinestomTag(key: Key): Tag<T>
    fun toPersistentType(): PersistentDataType<*, T>

    companion object {
        fun <T : Any> SerializationType<T>.toEntry(key: Key) = SerializationEntry(key, this)
        fun <T : Any> SerializationType<T>.toEntry(key: String) = toEntry(KeyUtils.of(key))
        fun <T : Any> SerializationType<T>.toPersistentDataEntry(key: Key)
            = PersistentDataEntry(key, this.toPersistentType())
        fun <T : Any> SerializationType<T>.list() = object : SerializationType<List<T>> {
            override fun toMinestomTag(key: Key) = this@list.toMinestomTag(key).list()
            override fun toPersistentType() = PersistentDataType.LIST.listTypeFrom(this@list.toPersistentType())
        }
        inline fun <P : Any, reified C : Any> SerializationType<P>.map(
            noinline readMap: (P) -> C, noinline writeMap: (C) -> P
        ) = object : SerializationType<C> {
            override fun toMinestomTag(key: Key) = this@map.toMinestomTag(key).map(readMap, writeMap)
            override fun toPersistentType() = mapPersistentType(this@map.toPersistentType(), readMap, writeMap)
        }

        inline fun <O : Any, P : Any, reified C : Any> mapPersistentType(
            base: PersistentDataType<O, P>,
            crossinline readMap: (P) -> C,
            crossinline writeMap: (C) -> P
        ): PersistentDataType<O, C> = object : PersistentDataType<O, C> {
            override fun getPrimitiveType(): Class<O> = base.primitiveType
            override fun getComplexType(): Class<C> = C::class.java
            override fun toPrimitive(complex: C, context: PersistentDataAdapterContext)
                    = base.toPrimitive(writeMap(complex), context)
            override fun fromPrimitive(primitive: O, context: PersistentDataAdapterContext)
                    = readMap(base.fromPrimitive(primitive, context))
        }
    }
}
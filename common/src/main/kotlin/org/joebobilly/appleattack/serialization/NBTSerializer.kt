package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagHandler
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import kotlin.reflect.KClass

abstract class NBTSerializer<T : Any>(private val klass : KClass<T>) : SerializationType<T> {
    abstract fun read(context: DeserializationContext): T
    abstract fun write(context: SerializationContext, value: T)

    override fun toMinestomTag(key: Key): Tag<T> = Tag.NBT(key.toString()).map({
        if(it !is CompoundBinaryTag) throw NBTReadError("", "not a compound")
        read(DeserializationContext.Minestom(it))
    }, {
        val handler = TagHandler.newHandler()
        write(SerializationContext.Minestom(handler), it)
        handler.asCompound()
    })
    override fun toPersistentType() = object : PersistentDataType<PersistentDataContainer, T> {
        override fun getPrimitiveType() = PersistentDataContainer::class.java
        override fun getComplexType() = this@NBTSerializer.klass.java
        override fun toPrimitive(complex: T, context: PersistentDataAdapterContext): PersistentDataContainer {
            val result = context.newPersistentDataContainer()
            write(SerializationContext.Paper(result), complex)
            return result
        }
        override fun fromPrimitive(primitive: PersistentDataContainer, context: PersistentDataAdapterContext): T {
            return read(DeserializationContext.Paper(primitive))
        }
    }
}
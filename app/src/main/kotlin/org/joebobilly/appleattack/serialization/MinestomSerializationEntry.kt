package org.joebobilly.appleattack.serialization

import com.google.common.base.Suppliers
import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagHandler

data class MinestomSerializationEntry<T : Any>(override val key: Key, override val type: SerializationType<T>)
    : SerializationEntry<T> {
    private val _minestomTag = Suppliers.memoize { convertToTag(key, type) }

    companion object {
        val <T : Any> SerializationEntry<T>.minestomTag: Tag<T> get() {
            check(this is MinestomSerializationEntry<T>) {
                "Serialization Entry is not of the Minestom kind..."
            }
            return this._minestomTag.get()
        }

        private fun <T : Any> convertToTag(key: Key, type: SerializationType<T>): Tag<T> {
            return when(type) {
                is PrimitiveSerializationType<T> -> {
                    @Suppress("UNCHECKED_CAST")
                    when(type) {
                        is PrimitiveSerializationType.ByteType -> Tag.Byte(key.toString())
                        is PrimitiveSerializationType.ShortType -> Tag.Short(key.toString())
                        is PrimitiveSerializationType.IntegerType -> Tag.Integer(key.toString())
                        is PrimitiveSerializationType.LongType -> Tag.Long(key.toString())
                        is PrimitiveSerializationType.FloatType -> Tag.Float(key.toString())
                        is PrimitiveSerializationType.DoubleType -> Tag.Double(key.toString())
                        is PrimitiveSerializationType.StringType -> Tag.String(key.toString())
                        is PrimitiveSerializationType.BooleanType -> Tag.Boolean(key.toString())
                    } as Tag<T>
                }
                is NBTSerializer<T> -> Tag.NBT(key.toString()).map({
                    if(it !is CompoundBinaryTag) throw NBTReadError("", "not a compound")
                    type.read(MinestomDeserializationContext(it))
                }, {
                    val handler = TagHandler.newHandler()
                    type.write(MinestomSerializationContext(handler), it)
                    handler.asCompound()
                })
                is SerializationType.Mapped<*, T> -> convertMappedToTag(key, type)
                is SerializationType.ListType<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    convertToTag(key, type.primitiveType).list() as Tag<T>
                }
            }
        }

        private fun <P : Any, C : Any> convertMappedToTag(key: Key, type: SerializationType.Mapped<P, C>): Tag<C> {
            return convertToTag(key, type.primitiveType).map(type.readMap, type.writeMap)
        }
    }
}
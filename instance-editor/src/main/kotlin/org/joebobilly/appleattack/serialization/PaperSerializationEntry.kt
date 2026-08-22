package org.joebobilly.appleattack.serialization

import com.google.common.base.Suppliers
import net.kyori.adventure.key.Key
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class PaperSerializationEntry<T : Any>(
    override val key: Key, override val type: SerializationType<T>
) : SerializationEntry<T> {
    private val _persistentDataEntry = Suppliers.memoize {
        PersistentDataEntry(key, convertToPersistentType(type))
    }

    companion object {
        val <T : Any> SerializationEntry<T>.persistentDataEntry: PersistentDataEntry<*, T> get() {
            check(this is PaperSerializationEntry<T>) {
                "Serialization Entry is not of the Paper kind..."
            }
            return this._persistentDataEntry.get()
        }

        private fun <T : Any> convertToPersistentType(type: SerializationType<T>): PersistentDataType<*, T> {
            return when(type) {
                is PrimitiveSerializationType<T> -> {
                    @Suppress("UNCHECKED_CAST")
                    when(type) {
                        is PrimitiveSerializationType.ByteType -> PersistentDataType.BYTE
                        is PrimitiveSerializationType.ShortType -> PersistentDataType.SHORT
                        is PrimitiveSerializationType.IntegerType -> PersistentDataType.INTEGER
                        is PrimitiveSerializationType.LongType -> PersistentDataType.LONG
                        is PrimitiveSerializationType.FloatType -> PersistentDataType.FLOAT
                        is PrimitiveSerializationType.DoubleType -> PersistentDataType.DOUBLE
                        is PrimitiveSerializationType.StringType -> PersistentDataType.STRING
                        is PrimitiveSerializationType.BooleanType -> PersistentDataType.BOOLEAN
                        is PrimitiveSerializationType.ByteArrayType -> PersistentDataType.BYTE_ARRAY
                        is PrimitiveSerializationType.IntArrayType -> PersistentDataType.INTEGER_ARRAY
                        is PrimitiveSerializationType.LongArrayType -> PersistentDataType.LONG_ARRAY
                    } as PersistentDataType<*, T>
                }
                is NBTSerializer<T> -> object : PersistentDataType<PersistentDataContainer, T> {
                    override fun getPrimitiveType() = PersistentDataContainer::class.java
                    override fun getComplexType() = type.klass.java
                    override fun toPrimitive(complex: T, context: PersistentDataAdapterContext): PersistentDataContainer {
                        val result = context.newPersistentDataContainer()
                        type.write(PaperSerializationContext(result), complex)
                        return result
                    }
                    override fun fromPrimitive(primitive: PersistentDataContainer, context: PersistentDataAdapterContext): T {
                        return type.read(PaperDeserializationContext(primitive))
                    }
                }
                is SerializationType.Mapped<*, T> -> convertMappedToType(type)
                is SerializationType.ListType<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    PersistentDataType.LIST.listTypeFrom(convertToPersistentType(type.primitiveType))
                        as PersistentDataType<*, T>
                }
            }
        }

        private fun <P : Any, C : Any> convertMappedToType(type: SerializationType.Mapped<P, C>): PersistentDataType<*, C> {
            return MappedPersistentDataType(
                convertToPersistentType(type.primitiveType), type.klass,
                type.readMap, type.writeMap
            )
        }
    }
}
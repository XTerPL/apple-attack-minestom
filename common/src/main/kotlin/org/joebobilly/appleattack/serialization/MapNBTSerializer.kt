package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.Key
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.utils.ClassUtils

class MapNBTSerializer<K : Any, V : Any>(
    private val valueType: SerializationType<V>,
    private val keyReadMap: (Key) -> K, private val keyWriteMap: (K) -> Key
) : NBTSerializer<Map<K, V>> {
    override val klass = ClassUtils.getKClass<Map<K, V>>()
    override fun read(context: DeserializationContext): Map<K, V> {
        val result = mutableMapOf<K, V>()
        for(key in context.getKeys()) {
            result[keyReadMap(key)] = context.read(valueType.toEntry(key))
        }
        return result
    }
    override fun write(context: SerializationContext, value: Map<K, V>) {
        for((key, value) in value) {
            context.write(valueType.toEntry(keyWriteMap(key)), value)
        }
    }
}
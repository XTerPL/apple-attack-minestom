package org.joebobilly.appleattack.items

import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTCopySerializer
import org.joebobilly.appleattack.serialization.SerializationContext

class AAItemMetaPair<METATYPE : Any>(val itemType: AAItem<METATYPE>, meta: METATYPE) {
    val meta = itemType.copyMeta(meta)
        get() = itemType.copyMeta(field)

    companion object {
        fun AAItemMetaPair<*>?.hasProperty(property: ItemProperty<*, *>): Boolean {
            if(this != null) return hasProperty(property)
            return property.default != null
        }
        fun <T> AAItemMetaPair<*>?.getProperty(property: ItemProperty<T, *>): T {
            if(this != null) return getProperty(property)
            return property.getDefaultOrThrow(null)
        }
    }

    fun create(count: Int = 1): ItemStack {
        return itemType.create(count, meta)
    }

    fun hasProperty(property: ItemProperty<*, *>): Boolean {
        return itemType.hasProperty(property)
    }
    // throws if property doesn't exist on this item type
    fun <T> getProperty(property: ItemProperty<T, *>): T {
        return itemType.getProperty(property, meta)
    }
    fun <T, U> withProperty(property: ItemProperty<T, *>, consumer: (T) -> U): U? {
        if(hasProperty(property)) {
            return consumer(getProperty(property))
        }
        return null
    }

    object Serializer : NBTCopySerializer<AAItemMetaPair<*>> {
        override val klass = AAItemMetaPair::class

        override fun read(context: DeserializationContext): AAItemMetaPair<*> {
            val itemType = context.read(AAItem.itemEntry)
            return readPair(context, itemType)
        }

        private fun <METATYPE : Any> readPair(context: DeserializationContext, itemType: AAItem<METATYPE>): AAItemMetaPair<*> {
            val meta = context.read(itemType.metaEntry)
            return AAItemMetaPair(itemType, meta)
        }

        override fun write(context: SerializationContext, value: AAItemMetaPair<*>) {
            writeMeta(context, value)
            context.write(AAItem.itemEntry, value.itemType)
        }

        private fun <METATYPE : Any> writeMeta(context: SerializationContext, value: AAItemMetaPair<METATYPE>) {
            context.write(value.itemType.metaEntry, value.meta)
        }

        override fun copy(value: AAItemMetaPair<*>): AAItemMetaPair<*> {
            return makeCopy(value)
        }

        private fun <METATYPE : Any> makeCopy(value: AAItemMetaPair<METATYPE>): AAItemMetaPair<METATYPE> {
            return AAItemMetaPair(value.itemType, value.meta)
        }
    }
}
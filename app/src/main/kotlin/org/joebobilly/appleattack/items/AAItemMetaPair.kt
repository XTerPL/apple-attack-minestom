package org.joebobilly.appleattack.items

import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagWritable
import org.joebobilly.appleattack.utils.TagCopySerializer
import org.joebobilly.appleattack.utils.TagUtils.getTagOrThrow

class AAItemMetaPair<METATYPE>(val itemType: AAItem<METATYPE>, meta: METATYPE) {
    val meta = itemType.copyMeta(meta)
        get() = itemType.copyMeta(field)

    companion object {
        fun tag(key: String): Tag<AAItemMetaPair<*>> {
            return Tag.Structure(key, Serializer)
        }

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

    object Serializer : TagCopySerializer<AAItemMetaPair<*>> {
        override fun read(reader: TagReadable): AAItemMetaPair<*> {
            val itemType = reader.getTagOrThrow(AAItem.itemTag)
            return readPair(reader, itemType)
        }

        private fun <METATYPE> readPair(reader: TagReadable, itemType: AAItem<METATYPE>): AAItemMetaPair<*> {
            val meta = reader.getTagOrThrow(itemType.metaTag)
            return AAItemMetaPair(itemType, meta)
        }

        override fun write(writer: TagWritable, value: AAItemMetaPair<*>) {
            writeMeta(writer, value)
            writer.setTag(AAItem.itemTag, value.itemType)
        }

        private fun <METATYPE> writeMeta(writer: TagWritable, value: AAItemMetaPair<METATYPE>) {
            writer.setTag(value.itemType.metaTag, value.meta)
        }

        override fun copy(value: AAItemMetaPair<*>): AAItemMetaPair<*> {
            return makeCopy(value)
        }

        private fun <METATYPE> makeCopy(value: AAItemMetaPair<METATYPE>): AAItemMetaPair<METATYPE> {
            return AAItemMetaPair(value.itemType, value.meta)
        }
    }
}
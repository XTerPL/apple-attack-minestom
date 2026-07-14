package org.joebobilly.appleattack.items

import net.minestom.server.adventure.MinestomAdventure
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagSerializer
import net.minestom.server.tag.TagWritable
import org.joebobilly.appleattack.utils.TagUtils
import java.util.logging.Logger

object ItemStackSerializer : TagSerializer<ItemStack> {
    private val count = Tag.Integer("count")
    private val invalidItemStackLogger = Logger.getLogger("invalid-item-stack")

    fun tag(key: String): Tag<ItemStack> {
        return TagUtils.structureSerializeEmptyTag(key, this)
    }

    override fun read(reader: TagReadable): ItemStack {
        val id = reader.getTag(AAItem.idTag) ?: return ItemStack.AIR
        val itemType = AAItemManager.getItem(id) ?: return ItemStack.AIR
        val count = reader.getTag(count)
        return readItem(reader, itemType, count)
    }

    private fun <METATYPE> readItem(reader: TagReadable, itemType: AAItem<METATYPE>, count: Int): ItemStack {
        val meta = reader.getTag(itemType.metaTag) ?: return ItemStack.AIR
        return itemType.create(count, meta)
    }

    override fun write(writer: TagWritable, value: ItemStack) {
        if(value.isAir) return
        val itemType = AAItemManager.getItem(value)
        if(itemType == null) {
            invalidItemStackLogger.severe("Unknown item stack serialized:\n"
                    + MinestomAdventure.tagStringIO().asString(value.toItemNBT()))
            return
        }
        if(!writeMeta(writer, value, itemType)) {
            return
        }
        writer.setTag(AAItem.itemTag, itemType)
        writer.setTag(count, value.amount())
    }

    private fun <METATYPE> writeMeta(writer: TagWritable, value: ItemStack, itemType: AAItem<METATYPE>): Boolean {
        val meta = itemType.getMeta(value)
        if(meta == null) {
            invalidItemStackLogger.severe("Invalid item meta found:\n"
                    + MinestomAdventure.tagStringIO().asString(value.toItemNBT()))
            return false
        }
        writer.setTag(itemType.metaTag, meta)
        return true
    }
}
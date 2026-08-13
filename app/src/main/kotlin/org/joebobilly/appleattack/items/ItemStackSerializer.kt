package org.joebobilly.appleattack.items

import net.minestom.server.adventure.MinestomAdventure
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTCopySerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes
import java.util.logging.Logger

object ItemStackSerializer : NBTCopySerializer<ItemStack>(ItemStack::class) {
    private val count = SerializationTypes.INTEGER.toEntry("count")
    private val invalidItemStackLogger = Logger.getLogger("invalid-item-stack")

    override fun read(context: DeserializationContext): ItemStack {
        val itemType = context.readNullable(AAItem.itemEntry) ?: return ItemStack.AIR
        val count = context.read(count, 1)
        return readItem(context, itemType, count)
    }

    private fun <METATYPE : Any> readItem(context: DeserializationContext, itemType: AAItem<METATYPE>, count: Int): ItemStack {
        val meta = context.read(itemType.metaEntry)
        return itemType.create(count, meta)
    }

    override fun write(context: SerializationContext, value: ItemStack) {
        if(value.isAir) return
        val itemType = AAItemManager.getItem(value)
        if(itemType == null) {
            invalidItemStackLogger.severe("Unknown item stack serialized:\n"
                    + MinestomAdventure.tagStringIO().asString(value.toItemNBT()))
            return
        }
        if(!writeMeta(context, value, itemType)) {
            return
        }
        context.write(AAItem.itemEntry, itemType)
        context.write(count, value.amount())
    }

    override fun copy(value: ItemStack): ItemStack {
        return value
    }

    private fun <METATYPE : Any> writeMeta(context: SerializationContext, value: ItemStack, itemType: AAItem<METATYPE>): Boolean {
        val meta = itemType.getMeta(value)
        if(meta == null) {
            invalidItemStackLogger.severe("Invalid item meta found:\n"
                    + MinestomAdventure.tagStringIO().asString(value.toItemNBT()))
            return false
        }
        context.write(itemType.metaEntry, meta)
        return true
    }
}
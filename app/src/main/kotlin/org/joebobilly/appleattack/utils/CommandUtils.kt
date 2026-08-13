package org.joebobilly.appleattack.utils

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import org.joebobilly.appleattack.items.AAItem
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.serialization.NBTReadError

object CommandUtils {
    fun <METATYPE : Any> parseItemMetaPair(sender: CommandSender?, itemType: AAItem<METATYPE>, metaNbt: CompoundBinaryTag) : AAItemMetaPair<METATYPE>? {
        try {
            val meta = itemType.deserializeMeta(metaNbt)
            return AAItemMetaPair(itemType, meta)
        }
        catch(e: NBTReadError) {
            sender?.sendMessage(
                Component.text(
                    "Invalid meta for item ${itemType.id}:\n" + e.getSourcedMessage(), NamedTextColor.RED)
            )
            return null
        }
        catch(e: Exception) {
            sender?.sendMessage(
                Component.text(
                    "Invalid meta for item ${itemType.id}:\n" + (e.message ?: "Unknown exception"), NamedTextColor.RED)
            )
            return null
        }
    }
}
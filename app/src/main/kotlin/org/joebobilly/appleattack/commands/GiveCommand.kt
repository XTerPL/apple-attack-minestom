package org.joebobilly.appleattack.commands

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import net.minestom.server.inventory.TransactionOption
import org.joebobilly.appleattack.items.AAItem
import org.joebobilly.appleattack.items.AAItemManager

object GiveCommand : Command("give") {
    init {
        defaultExecutor = { sender: CommandSender?, _: CommandContext ->
            sender?.sendMessage(Component.text("Syntax: /give <id> [count] [meta nbt]", NamedTextColor.RED))
        }

        val idArgument = ArgumentType.String("id")
        idArgument.suggestionCallback = {
            _, _, suggestion ->
            AAItemManager.getSuggestions(suggestion.input).forEach {
                entry -> suggestion.addEntry(entry)
            }
        }

        val countArgument = ArgumentType.Integer("count").setDefaultValue(1)

        val metaNbtArgument = ArgumentType.NbtCompound("meta_nbt").setDefaultValue(CompoundBinaryTag.empty())

        addSyntax({ sender: CommandSender?, ctx: CommandContext ->
            if(sender is Player) {
                val id = ctx.get(idArgument)
                val itemType = AAItemManager.get(id)
                if(itemType == null) {
                    sender.sendMessage(Component.text("$id is not a valid item type!", NamedTextColor.RED))
                    return@addSyntax
                }
                val count = ctx.get(countArgument)
                if(count !in 1..itemType.maxCount) {
                    sender.sendMessage(Component.text("Count needs to be between 1 and ${itemType.maxCount}, found $count!", NamedTextColor.RED))
                    return@addSyntax
                }
                val metaNbt = ctx.get(metaNbtArgument)
                try {
                    giveItem(sender, itemType, count, metaNbt)
                }
                catch(e: Exception) {
                    sender.sendMessage(
                        Component.text(
                            "Invalid meta for item $id:\n" + (e.message ?: "Unknown exception"), NamedTextColor.RED)
                    )
                }
            }
            else {
                sender?.sendMessage(Component.text("This command needs to be performed by a player.", NamedTextColor.RED))
            }
        }, idArgument, countArgument, metaNbtArgument)
    }

    private fun <METATYPE> giveItem(player: Player, itemType: AAItem<METATYPE>, count: Int, metaNbt: CompoundBinaryTag) {
        val meta = itemType.deserializeMeta(metaNbt)
        if(meta == null) {
            player.sendMessage(Component.text("Invalid meta for item ${itemType.id}", NamedTextColor.RED))
            return
        }
        val itemStack = itemType.create(count, meta)
        val remainingCount = player.inventory.addItemStack(itemStack, TransactionOption.ALL).amount()
        val given = count - remainingCount
        if(given > 0) {
            player.sendMessage(Component.text("Given $given x ${itemType.id}", NamedTextColor.GREEN))
        }
        else {
            player.sendMessage(Component.text("Failed to give any ${itemType.id}", NamedTextColor.RED))
        }
    }
}
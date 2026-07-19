package org.joebobilly.appleattack.commands

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.items.tools.type.ToolItem
import org.joebobilly.appleattack.utils.CommandUtils

object AddUpgradeCommand : Command("addupgrade") {
    init {
        defaultExecutor = { sender: CommandSender?, _: CommandContext ->
            sender?.sendMessage(Component.text("Syntax: /addupgrade <id> [meta nbt]", NamedTextColor.RED))
        }

        val idArgument = ArgumentType.String("id")
        idArgument.suggestionCallback = {
            _, _, suggestion ->
            AAItemManager.getSuggestions(suggestion.input).forEach {
                entry -> suggestion.addEntry(entry)
            }
        }

        val metaNbtArgument = ArgumentType.NbtCompound("meta_nbt").setDefaultValue(CompoundBinaryTag.empty())

        addSyntax({ sender: CommandSender?, ctx: CommandContext ->
            if(sender is Player) {
                val id = ctx.get(idArgument)
                val itemType = AAItemManager.get(id)
                if(itemType == null) {
                    sender.sendMessage(Component.text("$id is not a valid item type!", NamedTextColor.RED))
                    return@addSyntax
                }
                val metaNbt = ctx.get(metaNbtArgument)
                val metaPair = CommandUtils.parseItemMetaPair(sender, itemType, metaNbt) ?: return@addSyntax
                val toolType = AAItemManager.getItem(sender.itemInMainHand)
                if(toolType is ToolItem<*>) {
                    upgradeTool(sender, toolType, metaPair)
                }
                else {
                    sender.sendMessage(Component.text("Cannot upgrade a non-tool item", NamedTextColor.RED))
                }
            }
            else {
                sender?.sendMessage(Component.text("This command needs to be performed by a player.", NamedTextColor.RED))
            }
        }, idArgument, metaNbtArgument)
    }

    private fun <T : ToolMeta> upgradeTool(player: Player, toolType: ToolItem<T>, upgrade: AAItemMetaPair<*>) {
        player.itemInMainHand = toolType.update(player.itemInMainHand) {
            if(toolType.safelyAddUpgradeToMeta(it, upgrade)) {
                player.sendMessage(Component.text("Upgraded tool with ${upgrade.itemType.id}", NamedTextColor.GREEN))
            }
            else {
                player.sendMessage(Component.text("Failed to upgrade tool with ${upgrade.itemType.id}", NamedTextColor.RED))
            }
            it
        }
    }
}
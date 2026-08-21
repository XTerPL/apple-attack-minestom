package org.joebobilly.appleattack.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.joebobilly.appleattack.serialization.PaperSerializationEntry.Companion.persistentDataEntry
import org.joebobilly.appleattack.utils.CommandUtils.filterByCommandInput
import org.joebobilly.appleattack.utils.PositionList
import org.joebobilly.appleattack.utils.Sounds

object PositionListCommand : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) {
            sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED))
            return true
        }
        val queue = CommandArgQueue(args)
        val subcommand = queue.readString()
        if(subcommand == null) {
            sender.give(PositionList.createPositionListItem())
            return true
        }

        val mainHand = sender.inventory.itemInMainHand
        val positionList = PositionList.POSITION_LIST_ENTRY.persistentDataEntry.get(mainHand.persistentDataContainer)
        if(positionList == null) {
            sender.sendMessage(Component.text("Cannot modify a non-position list item", NamedTextColor.RED))
            return true
        }

        val result = when(subcommand) {
            "remove" -> removeSubcommand(positionList, sender, queue)
            else -> SubCommandResult.SYNTAX_ERROR
        }
        if(result != SubCommandResult.SUCCESS) return result == SubCommandResult.ERROR
        mainHand.editPersistentDataContainer {
            PositionList.POSITION_LIST_ENTRY.persistentDataEntry.set(it, positionList)
        }
        PositionList.updatePositionListItem(mainHand)
        return true
    }

    private fun removeSubcommand(
        positionList: PositionList, sender: CommandSender, queue: CommandArgQueue
    ): SubCommandResult {
        try {
            val index = queue.readInt() ?: return SubCommandResult.SYNTAX_ERROR
            if(!positionList.removePositionAt(index)) {
                sender.sendMessage(Component.text("Invalid index $index", NamedTextColor.RED))
                return SubCommandResult.ERROR
            }
        }
        catch(_: NumberFormatException) {
            sender.sendMessage(Component.text("Index ${queue.readString()} has to be a number", NamedTextColor.RED))
            return SubCommandResult.ERROR
        }

        sender.playSound(Sounds.POSITION_REMOVE)

        return SubCommandResult.SUCCESS
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        if(args.size == 1) {
            return listOf("remove").filterByCommandInput(args[0])
        }
        return null
    }
}
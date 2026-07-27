package org.joebobilly.appleattack.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.joebobilly.appleattack.utils.PositionList
import org.joebobilly.appleattack.utils.Sounds
import org.joebobilly.appleattack.utils.SubCommandResult

class PositionListCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) {
            sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED))
            return true
        }
        if(args.count() > 0) {
            val mainHand = sender.inventory.itemInMainHand
            val positionList = PositionList.POSITION_LIST_ENTRY.get(mainHand.persistentDataContainer)
            if(positionList == null) {
                sender.sendMessage(Component.text("Cannot modify a non-position list item", NamedTextColor.RED))
                return true
            }

            val result = when(args[0]) {
                "remove" -> removeSubcommand(positionList, sender, args)
                else -> SubCommandResult.SYNTAX_ERROR
            }
            if(result != SubCommandResult.SUCCESS) return result == SubCommandResult.ERROR
            mainHand.editPersistentDataContainer {
                PositionList.POSITION_LIST_ENTRY.set(it, positionList)
            }
            PositionList.updatePositionListItem(mainHand)
            return true
        }
        sender.give(PositionList.createPositionListItem())
        return true
    }

    private fun removeSubcommand(
        positionList: PositionList, sender: CommandSender, args: Array<out String>
    ): SubCommandResult {
        if(args.count() != 2) return SubCommandResult.SYNTAX_ERROR

        try {
            val index = args[1].toInt()
            if(!positionList.removePositionAt(index)) {
                sender.sendMessage(Component.text("Invalid index $index", NamedTextColor.RED))
                return SubCommandResult.ERROR
            }
        }
        catch(_: NumberFormatException) {
            sender.sendMessage(Component.text("Index ${args[1]} has to be a number", NamedTextColor.RED))
            return SubCommandResult.ERROR
        }

        sender.playSound(Sounds.POSITION_REMOVE)
        return SubCommandResult.SUCCESS
    }
}
package org.joebobilly.appleattack.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.utils.location.RelativeVec
import org.joebobilly.appleattack.entities.AAEntityTypeManager

object SpawnCommand : Command("spawn", "summon") {
    init {
        defaultExecutor = { sender: CommandSender?, _: CommandContext ->
            sender?.sendMessage(Component.text("Syntax: /spawn <id> [position]", NamedTextColor.RED))
        }

        val idArgument = ArgumentType.String("id")
        idArgument.suggestionCallback = {
            _, _, suggestion ->
            AAEntityTypeManager.getSuggestions(suggestion.input).forEach {
                entry -> suggestion.addEntry(entry)
            }
        }

        val positionArgument = ArgumentType.RelativeVec3("position").setDefaultValue(
            RelativeVec(Vec(0.0, 0.0, 0.0),
                RelativeVec.CoordinateType.RELATIVE, true, true, true
            )
        )

        addSyntax({ sender: CommandSender?, ctx: CommandContext ->
            if(sender is Player) {
                val id = ctx.get(idArgument)
                val entityType = AAEntityTypeManager.get(id)
                if(entityType == null) {
                    sender.sendMessage(Component.text("$id is not a valid entity type!", NamedTextColor.RED))
                }
                else {
                    val position = ctx.get(positionArgument).from(sender)
                    entityType.spawn(sender.instance, position.asPos())
                }
            }
            else {
                sender?.sendMessage(Component.text("This command needs to be performed by a player.", NamedTextColor.RED))
            }
        }, idArgument, positionArgument)
    }
}
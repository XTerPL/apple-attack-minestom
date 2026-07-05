package org.joebobilly.appleattack.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.MinestomAdventure
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.entity.Player

object LookNBTCommand : Command("looknbt") {
    init {
        defaultExecutor = { sender: CommandSender?, _: CommandContext ->
            sender?.sendMessage(Component.text("Syntax: /looknbt", NamedTextColor.RED))
        }
        addSyntax({ sender: CommandSender?, _: CommandContext ->
            if(sender is Player) {
                val tag = sender.instance.getBlock(sender.getTargetBlockPosition(5)).nbt()
                if(tag != null) {
                    sender.sendMessage(Component.text(MinestomAdventure.tagStringIO().asString(tag), NamedTextColor.YELLOW))
                }
                else {
                    sender.sendMessage(Component.text("No NBT found!", NamedTextColor.RED))
                }
            }
        })
    }
}
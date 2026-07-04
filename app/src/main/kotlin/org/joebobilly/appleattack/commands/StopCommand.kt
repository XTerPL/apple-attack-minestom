package org.joebobilly.appleattack.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext

class StopCommand : Command("stop") {
    init {
        defaultExecutor = { sender: CommandSender?, _: CommandContext ->
            sender?.sendMessage(Component.text("Syntax: /stop", NamedTextColor.RED))
        }
        addSyntax({ _: CommandSender?, _: CommandContext ->
            MinecraftServer.getSchedulerManager().scheduleEndOfTick(MinecraftServer::stopCleanly)
        })
    }
}
package org.joebobilly.appleattack.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.timer.ExecutionType

object StopCommand : Command("stop") {
    private val kickMessage = Component.text("Server shutting down", NamedTextColor.RED)

    init {
        defaultExecutor = { sender: CommandSender?, _: CommandContext ->
            sender?.sendMessage(Component.text("Syntax: /stop", NamedTextColor.RED))
        }
        addSyntax({ _: CommandSender?, _: CommandContext ->
            MinecraftServer.getConnectionManager().configPlayers.forEach {
                it.kick(kickMessage)
            }
            MinecraftServer.getConnectionManager().onlinePlayers.forEach {
                it.kick(kickMessage)
            }
            MinecraftServer.getSchedulerManager().scheduleNextTick(MinecraftServer::stopCleanly, ExecutionType.TICK_END)
        })
    }
}
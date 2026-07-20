package org.joebobilly.appleattack.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import org.joebobilly.appleattack.interfaces.forge.StationInterface
import org.joebobilly.appleattack.interfaces.forge.SwordForgeInterface

object ForgeCommand : Command("forge") {
    private val subcommands = mapOf(
        Pair("station") { StationInterface() },
        Pair("sword") { SwordForgeInterface() }
    )

    init {
        defaultExecutor = { sender: CommandSender?, _: CommandContext ->
            sender?.sendMessage(Component.text("Syntax: /forge (sword)", NamedTextColor.RED))
        }

        val submenuArgument = ArgumentType.Word("submenu").from(*subcommands.keys.toTypedArray())

        addSyntax({ sender: CommandSender?, ctx: CommandContext ->
            if(sender is Player) {
                subcommands[ctx.get(submenuArgument)]?.apply {
                    sender.openInventory(this().inventory)
                }
            }
            else {
                sender?.sendMessage(Component.text("This command needs to be performed by a player.", NamedTextColor.RED))
            }
        }, submenuArgument)
    }
}
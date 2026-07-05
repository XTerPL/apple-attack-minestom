package org.joebobilly.appleattack

import net.kyori.adventure.key.Key
import net.minestom.server.Auth
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.InstanceContainer
import org.joebobilly.appleattack.blockhandlers.BannerBlockHandler
import org.joebobilly.appleattack.blockhandlers.SignBlockHandler
import org.joebobilly.appleattack.blockhandlers.SkullBlockHandler
import org.joebobilly.appleattack.commands.LookNBTCommand
import org.joebobilly.appleattack.commands.StopCommand
import java.nio.file.Path

fun main() {
    val minecraftServer = MinecraftServer.init(Auth.Online())

    val blockManager = MinecraftServer.getBlockManager()
    blockManager.registerHandler(SignBlockHandler.Regular.key) {
        SignBlockHandler.Regular
    }
    blockManager.registerHandler(SignBlockHandler.Hanging.key) {
        SignBlockHandler.Hanging
    }
    blockManager.registerHandler(SkullBlockHandler.key) {
        SkullBlockHandler
    }
    blockManager.registerHandler(BannerBlockHandler.key) {
        BannerBlockHandler
    }

    val instanceManager = MinecraftServer.getInstanceManager()
    val instance: InstanceContainer = instanceManager.createInstanceContainer()
    val cwd = Path.of("").toAbsolutePath()
    println("Working directory: $cwd")

    val worldPath = Path.of("worlds/main").toAbsolutePath()
    println("World path: $worldPath")

    instance.chunkLoader = AnvilLoader(worldPath)

    val spawnPoint = Pos(-8.0, 57.0, 64.0)

    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        event.spawningInstance = instance
        event.player.respawnPoint = spawnPoint
        event.player.gameMode = GameMode.ADVENTURE
    }

    val commandManager = MinecraftServer.getCommandManager()
    commandManager.register(StopCommand)
    commandManager.register(LookNBTCommand)

    minecraftServer.start("0.0.0.0", 25565)
}
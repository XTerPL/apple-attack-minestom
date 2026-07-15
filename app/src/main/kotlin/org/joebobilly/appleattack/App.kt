package org.joebobilly.appleattack

import net.kyori.adventure.text.Component
import net.minestom.server.Auth
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.network.ConnectionState
import org.joebobilly.appleattack.blockhandlers.BannerBlockHandler
import org.joebobilly.appleattack.blockhandlers.SignBlockHandler
import org.joebobilly.appleattack.blockhandlers.SkullBlockHandler
import org.joebobilly.appleattack.commands.GiveCommand
import org.joebobilly.appleattack.commands.LookNBTCommand
import org.joebobilly.appleattack.commands.SpawnCommand
import org.joebobilly.appleattack.commands.StopCommand
import org.joebobilly.appleattack.content.items.AppleItem
import org.joebobilly.appleattack.content.mobs.AppleMob
import org.joebobilly.appleattack.events.DamageEvents
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.mobs.AAMobTypeManager
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.players.PlayerSaveManager
import java.nio.file.Path

fun main() {
    AAItemManager.register(AppleItem)
    AAItemManager.freeze()

    AAMobTypeManager.register(AppleMob)
    AAMobTypeManager.freeze()

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
        if(PlayerSaveManager.loadPlayer(event.player)) {
            event.spawningInstance = instance
            event.player.respawnPoint = spawnPoint
            event.player.gameMode = GameMode.ADVENTURE
        }
        else {
            event.player.kick(Component.text("Failed to load your progress! (notify an admin)"))
        }
    }
    globalEventHandler.addListener(PlayerDisconnectEvent::class.java) { event ->
        println("Player ${event.player.username} (${event.player.uuid}) disconnected!")
        if(event.player.playerConnection.serverState == ConnectionState.PLAY) {
            PlayerSaveManager.savePlayer(event.player)
        }
    }
    DamageEvents.init(globalEventHandler)

    val commandManager = MinecraftServer.getCommandManager()
    commandManager.register(StopCommand)
    commandManager.register(LookNBTCommand)
    commandManager.register(GiveCommand)
    commandManager.register(SpawnCommand)

    MinecraftServer.getConnectionManager().setPlayerProvider { connection, gameProfile -> AAPlayer(connection, gameProfile) }

    minecraftServer.start("0.0.0.0", 25565)
}
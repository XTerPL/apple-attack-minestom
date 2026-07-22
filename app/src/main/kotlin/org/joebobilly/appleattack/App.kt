package org.joebobilly.appleattack

import net.kyori.adventure.text.Component
import net.minestom.server.Auth
import net.minestom.server.MinecraftServer
import net.minestom.server.ServerFlag
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.entity.EntityVelocityEvent
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.network.ConnectionState
import org.joebobilly.appleattack.blockhandlers.BannerBlockHandler
import org.joebobilly.appleattack.blockhandlers.SignBlockHandler
import org.joebobilly.appleattack.blockhandlers.SkullBlockHandler
import org.joebobilly.appleattack.commands.AddUpgradeCommand
import org.joebobilly.appleattack.commands.ForgeCommand
import org.joebobilly.appleattack.commands.GiveCommand
import org.joebobilly.appleattack.commands.LookNBTCommand
import org.joebobilly.appleattack.commands.SpawnCommand
import org.joebobilly.appleattack.commands.StopCommand
import org.joebobilly.appleattack.content.items.Items
import org.joebobilly.appleattack.content.entities.mobs.AppleMob
import org.joebobilly.appleattack.content.entities.mobs.Mobs
import org.joebobilly.appleattack.content.entities.npcs.NPCs
import org.joebobilly.appleattack.content.entities.npcs.TestNPC
import org.joebobilly.appleattack.content.traits.Traits
import org.joebobilly.appleattack.entities.AAEntityTypeManager
import org.joebobilly.appleattack.events.DamageEvents
import org.joebobilly.appleattack.events.InstanceEvents
import org.joebobilly.appleattack.events.InventoryEvents
import org.joebobilly.appleattack.events.ItemEvents
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.items.tools.traits.TraitManager
import org.joebobilly.appleattack.events.InteractEvents
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.players.PlayerSaveManager
import org.joebobilly.appleattack.entities.spawners.MobSpawner
import org.joebobilly.appleattack.entities.spawners.NPCSpawner
import org.joebobilly.appleattack.entities.spawners.SpawnerManager
import java.nio.file.Path

fun main() {
    if(!ServerFlag.SERIALIZE_EMPTY_COMPOUND) {
        println("Please set the system variable 'minestom.serialization.serialize-empty-nbt-compound' to true")
        return
    }

    Items.register()
    AAItemManager.freeze()

    Mobs.register()
    NPCs.register()
    AAEntityTypeManager.freeze()

    Traits.register()
    TraitManager.freeze()

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

    // the instance events have to be registered before the instance gets registered
    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    DamageEvents.init(globalEventHandler)
    ItemEvents.init(globalEventHandler)
    InventoryEvents.init(globalEventHandler)
    InstanceEvents.init(globalEventHandler)
    InteractEvents.init(globalEventHandler)

    val instanceManager = MinecraftServer.getInstanceManager()
    val instance: InstanceContainer = instanceManager.createInstanceContainer()
    val cwd = Path.of("").toAbsolutePath()
    println("Working directory: $cwd")

    val worldPath = Path.of("worlds/main").toAbsolutePath()
    println("World path: $worldPath")

    instance.chunkLoader = AnvilLoader(worldPath)

    SpawnerManager.registerSpawner(
        MobSpawner(AppleMob, 5, listOf(
            Pos(-20.5, 57.0, 88.5),
            Pos(-15.5, 57.0, 86.5),
            Pos(-24.5, 57.0, 83.5)
        )), instance
    )
    SpawnerManager.registerSpawner(
        NPCSpawner(TestNPC, Pos(-16.5, 57.0, 55.5)), instance
    )

    val spawnPoint = Pos(-8.0, 57.0, 64.0)

    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) {
        if(PlayerSaveManager.loadPlayer(it.player)) {
            it.spawningInstance = instance
            it.player.respawnPoint = spawnPoint
            it.player.gameMode = GameMode.ADVENTURE
        }
        else {
            it.player.kick(Component.text("Failed to load your progress! (notify an admin)"))
        }
    }
    globalEventHandler.addListener(PlayerDisconnectEvent::class.java) {
        println("Player ${it.player.username} (${it.player.uuid}) disconnected!")
        if(it.player.playerConnection.serverState == ConnectionState.PLAY) {
            PlayerSaveManager.savePlayer(it.player)
        }
    }
    globalEventHandler.addListener(EntityVelocityEvent::class.java) {
        if(it.velocity.x().isNaN() || it.velocity.y().isNaN() || it.velocity.z().isNaN()) {
            println("Invalid velocity set to ${it.entity.uuid}, found ${it.velocity}")
            it.isCancelled = true
            return@addListener
        }
        if(it.velocity.x().isInfinite() || it.velocity.y().isInfinite() || it.velocity.z().isInfinite()) {
            println("Invalid velocity set to ${it.entity.uuid}, found ${it.velocity}")
            it.isCancelled = true
            return@addListener
        }
    }

    val schedulerManager = MinecraftServer.getSchedulerManager()
    SpawnerManager.init(schedulerManager)

    val commandManager = MinecraftServer.getCommandManager()
    commandManager.register(StopCommand)
    commandManager.register(LookNBTCommand)
    commandManager.register(GiveCommand)
    commandManager.register(SpawnCommand)
    commandManager.register(AddUpgradeCommand)
    commandManager.register(ForgeCommand)

    MinecraftServer.getConnectionManager().setPlayerProvider { connection, gameProfile -> AAPlayer(connection, gameProfile) }

    minecraftServer.start("0.0.0.0", 25565)
}
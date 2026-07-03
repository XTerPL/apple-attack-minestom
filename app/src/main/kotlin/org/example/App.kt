package org.example

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.InstanceContainer
import java.nio.file.Path

fun main() {
    val minecraftServer = MinecraftServer.init()

    val instanceManager = MinecraftServer.getInstanceManager()
    val instance: InstanceContainer = instanceManager.createInstanceContainer()
    val cwd = Path.of("").toAbsolutePath()
    println("Working directory: $cwd")

    val worldPath = Path.of("worlds/main").toAbsolutePath()
    println("World path: $worldPath")


    instance.setChunkLoader(AnvilLoader(worldPath))

    instance.setChunkLoader(AnvilLoader(Path.of("worlds/main")))

    val spawnPoint = Pos(-8.0, 57.0, 64.0)

    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        event.spawningInstance = instance
        event.player.respawnPoint = spawnPoint
    }

    minecraftServer.start("0.0.0.0", 25565)
}
package org.joebobilly.appleattack

import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry
import org.bukkit.plugin.java.JavaPlugin
import org.joebobilly.appleattack.commands.CreateEntitySpawnerCommand
import org.joebobilly.appleattack.commands.PositionListCommand
import org.joebobilly.appleattack.entities.spawners.SpawnerManager
import org.joebobilly.appleattack.holograms.HologramManager
import org.joebobilly.appleattack.infodump.InfoDump
import org.joebobilly.appleattack.listeners.PositionListListener
import org.joebobilly.appleattack.serialization.PaperDeserializationContext
import java.io.DataInputStream

class InstanceEditor : JavaPlugin() {
    companion object {
        fun get() = getProvidingPlugin(InstanceEditor::class.java) as InstanceEditor
        val logger get() = get().logger
        private lateinit var _infoDump: InfoDump
        val infoDump get() = _infoDump
    }

    override fun onLoad() {
        Platform.setPlatform(PaperPlatform)

        try {
            val stream = this.classLoader.getResourceAsStream("info_dump.nbt")
            if(stream == null) {
                _infoDump = InfoDump()
                logger.severe("Info Dump could not be loaded for some reason...")
            }
            else {
                _infoDump = stream.use {
                    val nbt = NbtIo.readCompressed(DataInputStream(stream), NbtAccounter.unlimitedHeap())
                    val pdc = CraftPersistentDataContainer(
                        mapOf(*nbt.entrySet().map { it.key to it.value }.toTypedArray()),
                        CraftPersistentDataTypeRegistry()
                    )
                    InfoDump.Serializer.read(PaperDeserializationContext(pdc))
                }
            }
        }
        catch(e: Exception) {
            _infoDump = InfoDump()
            logger.severe("Info Dump could not be loaded for some reason...")
            e.printStackTrace()
        }
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(PositionListListener, this)
        server.pluginManager.registerEvents(HologramManager, this)
        server.pluginManager.registerEvents(SpawnerManager, this)
        this.getCommand("positionlist")?.setExecutor(PositionListCommand)
        this.getCommand("createentityspawner")?.setExecutor(CreateEntitySpawnerCommand)
        SpawnerManager.update()
        logger.info("Enabled instance-editor")
    }

    override fun onDisable() {
        HologramManager.shutdown()
        logger.info("Disabled instance-editor")
    }
}

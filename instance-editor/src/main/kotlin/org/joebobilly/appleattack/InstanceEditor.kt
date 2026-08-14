package org.joebobilly.appleattack

import org.bukkit.plugin.java.JavaPlugin
import org.joebobilly.appleattack.commands.PositionListCommand
import org.joebobilly.appleattack.holograms.HologramManager
import org.joebobilly.appleattack.listeners.PositionListListener

class InstanceEditor : JavaPlugin() {
    companion object {
        fun get() = getProvidingPlugin(InstanceEditor::class.java) as InstanceEditor
        val logger get() = get().logger
    }

    override fun onLoad() {
        Platform.setPlatform(PaperPlatform)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(PositionListListener, this)
        server.pluginManager.registerEvents(HologramManager, this)
        this.getCommand("positionlist")?.setExecutor(PositionListCommand())
        logger.info("Enabled instance-editor")
    }

    override fun onDisable() {
        logger.info("Disabled instance-editor")
    }
}

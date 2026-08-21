package org.joebobilly.appleattack.holograms

import net.thenextlvl.hologram.Hologram
import org.bukkit.entity.Player

class HologramPlayer internal constructor(val player: Player) : HologramDisplayMap.KeyHologramDisplayMap() {
    override val namespace: String = "player--" + player.uniqueId.toString()
    override fun prepareHologram(hologram: Hologram) {
        hologram.isVisibleByDefault = false
        hologram.addViewer(player.uniqueId)
    }
}
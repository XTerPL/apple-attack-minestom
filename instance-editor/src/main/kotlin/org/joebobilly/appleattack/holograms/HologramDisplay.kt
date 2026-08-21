package org.joebobilly.appleattack.holograms

import net.thenextlvl.hologram.Hologram
import org.bukkit.Location

interface HologramDisplay {
    fun addHologram(position: Location, builder: (Hologram) -> Unit)
    fun remove()
    val isRemoved: Boolean
}
package org.joebobilly.appleattack.holograms

import net.kyori.adventure.key.Key
import net.thenextlvl.hologram.Hologram
import net.thenextlvl.hologram.HologramProvider
import org.bukkit.Location
import org.bukkit.entity.Player
import org.joebobilly.appleattack.holograms.HologramManager.ifHasHologram

class HologramPlayer internal constructor(val player: Player) {
    inner class Display internal constructor(val key: Key) {
        private var removed = false
        private var hologramsAdded = 0
        private val holograms = mutableListOf<String>()

        fun addHologram(position: Location, builder: (Hologram) -> Unit) {
            check(!removed) { "Already removed!" }
            val name = "player--" + player.uniqueId.toString() + "--" + key.namespace() + "--" + key.value() + "--" + hologramsAdded
            hologramsAdded++
            val hologram = HologramProvider.instance().createHologram(name, position)
            builder(hologram)
            hologram.isPersistent = false
            hologram.isVisibleByDefault = false
            hologram.addViewer(player.uniqueId)
            holograms.add(name)
        }

        internal fun onQuit() {
            removed = true

            val instance = HologramProvider.instance()
            holograms.forEach {
                instance.ifHasHologram(it) {
                    hologram -> instance.deleteHologram(hologram)
                }
            }
            holograms.clear()
        }
    }

    private val displays = mutableMapOf<Key, Display>()

    fun createDisplay(key: Key): Display {
        if(displays.containsKey(key)) {
            displays[key]!!.onQuit()
        }
        displays[key] = Display(key)
        return displays[key]!!
    }

    fun removeDisplay(key: Key) {
        if(displays.containsKey(key)) {
            displays[key]!!.onQuit()
            displays.remove(key)
        }
    }

    internal fun onQuit() {
        displays.values.forEach {
            it.onQuit()
        }
        displays.clear()
    }
}
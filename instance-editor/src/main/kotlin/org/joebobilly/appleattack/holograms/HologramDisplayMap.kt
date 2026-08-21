package org.joebobilly.appleattack.holograms

import net.kyori.adventure.key.Key
import net.thenextlvl.hologram.Hologram
import net.thenextlvl.hologram.HologramProvider
import org.bukkit.Location
import org.joebobilly.appleattack.holograms.HologramManager.ifHasHologram

abstract class HologramDisplayMap<T> {
    abstract class KeyHologramDisplayMap : HologramDisplayMap<Key>() {
        final override fun stringifyIdentifier(identifier: Key) = identifier.namespace() + "--" + identifier.value()
    }

    internal inner class Display(val identifier: T) : HologramDisplay {
        private var removed = false
        private var hologramsAdded = 0
        private val holograms = mutableListOf<String>()

        override fun addHologram(position: Location, builder: (Hologram) -> Unit) {
            check(!removed) { "Already removed!" }
            val name = "apple_attack--" + namespace + "--" + stringifyIdentifier(identifier) + "--" + hologramsAdded
            hologramsAdded++
            val hologram = HologramProvider.instance().createHologram(name, position)
            builder(hologram)
            prepareHologram(hologram)
            hologram.isPersistent = false
            holograms.add(name)
            hologram.spawn()
        }

        override fun remove() {
            check(!removed) { "Already removed!" }
            displays.remove(identifier)
            onRemove()
        }

        override val isRemoved get() = removed

        internal fun onRemove() {
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

    protected abstract val namespace: String
    protected abstract fun stringifyIdentifier(identifier: T): String
    protected open fun prepareHologram(hologram: Hologram) {}

    private val displays = mutableMapOf<T, Display>()

    fun createDisplay(identifier: T): HologramDisplay {
        if(displays.containsKey(identifier)) {
            displays[identifier]!!.onRemove()
        }
        displays[identifier] = Display(identifier)
        return displays[identifier]!!
    }

    fun getDisplay(identifier: T): HologramDisplay? = displays[identifier]

    fun removeDisplay(identifier: T) {
        displays[identifier]?.remove()
    }

    fun cleanup() {
        for(display in displays.values) {
            display.onRemove()
        }
        displays.clear()
    }
}
package org.joebobilly.appleattack.holograms

import net.thenextlvl.hologram.Hologram
import net.thenextlvl.hologram.HologramProvider
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

object HologramManager : Listener {
    private val hologramPlayers = mutableMapOf<UUID, HologramPlayer>()

    fun getHologramPlayer(player: Player): HologramPlayer {
        hologramPlayers.putIfAbsent(player.uniqueId, HologramPlayer(player))
        return hologramPlayers[player.uniqueId]!!
    }

    fun HologramProvider.ifHasHologram(name: String, consumer: (Hologram) -> Unit) {
        this.getHologram(name).ifPresent(consumer)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val hologramPlayer = hologramPlayers.remove(event.player.uniqueId) ?: return
        hologramPlayer.onQuit()
    }
}
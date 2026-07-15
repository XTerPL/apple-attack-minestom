package org.joebobilly.appleattack.players

import net.minestom.server.entity.Player
import net.minestom.server.network.player.GameProfile
import net.minestom.server.network.player.PlayerConnection
import org.joebobilly.appleattack.damage.EntityHealth
import org.joebobilly.appleattack.mobs.AAMobType

class AAPlayer(playerConnection: PlayerConnection, gameProfile: GameProfile) : Player(playerConnection, gameProfile) {
    val health = EntityHealth(this, this::maxHealth)
    var trackedHealth: EntityHealth? = null
        set(value) {
            if(field == value) {
                return
            }
            field?.bossBar?.removeViewer(this)
            value?.bossBar?.addViewer(this)
            field = value
        }
    private var lastRegenTick = 0L

    fun maxHealth(): Double = 20.0

    override fun kill() {
        health.healFull()
        instance.playSound(AAMobType.defaultDeathSound, position)
        teleport(respawnPoint)
        playSound(AAMobType.defaultDeathSound)
    }

    override fun update(time: Long) {
        super.update(time)

        if(trackedHealth != null && trackedHealth?.entity?.instance == null) {
            trackedHealth = null
        }

        if(time - lastRegenTick >= 3000L) {
            lastRegenTick = time
            health.heal(1.0)
        }

        sendActionBar(health.getHealthDisplay())
    }
}
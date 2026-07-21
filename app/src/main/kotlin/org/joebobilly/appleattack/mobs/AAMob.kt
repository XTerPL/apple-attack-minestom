package org.joebobilly.appleattack.mobs

import net.minestom.server.component.DataComponents
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityCreature
import org.joebobilly.appleattack.damage.EntityHealth
import org.joebobilly.appleattack.rewards.Reward.Companion.spawnEntities

class AAMob(val type: AAMobType) : EntityCreature(type.startingEntityType) {
    val health = EntityHealth(this, type::maxHealth)
    var homePosition: Vec? = null

    init {
        set(DataComponents.CUSTOM_NAME, type.entityName())
        type.onInit(this)
    }

    override fun update(time: Long) {
        super.update(time)
        if(type.shouldDespawn(instance, position)) {
            remove()
        }
    }

    override fun kill() {
        super.kill()
        instance.playSound(type.deathSound(), this)
        val lootTable = type.onKill(this)
        lootTable.getRewards().forEach {
            it.spawnEntities(instance, position.add(0.0, 0.5, 0.0))
        }
    }
}
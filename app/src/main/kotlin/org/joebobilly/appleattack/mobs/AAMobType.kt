package org.joebobilly.appleattack.mobs

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import org.joebobilly.appleattack.damage.AttackInfo
import org.joebobilly.appleattack.rewards.LootTable

abstract class AAMobType(val id: String, val startingEntityType: EntityType) {
    companion object {
        val defaultDamageSound = Sound.sound(
            Key.key("entity.generic.hurt"), Sound.Source.HOSTILE, 1f, 1f
        )
        val defaultDeathSound = Sound.sound(
            Key.key("entity.generic.death"), Sound.Source.HOSTILE, 1f, 1f
        )
    }

    abstract fun entityName(): Component
    abstract fun maxHealth(): Double
    open fun despawnRadius(): Double? = 32.0
    open fun damageSound(): Sound = defaultDamageSound
    open fun deathSound(): Sound = defaultDeathSound
    open fun meleeAttack(entity: AAMob): AttackInfo = AttackInfo.melee(1.0)

    open fun onInit(entity: AAMob) {

    }

    open fun onKill(entity: AAMob): LootTable {
        return LootTable()
    }

    fun create(): AAMob {
        return AAMob(this)
    }

    fun spawn(instance: Instance, position: Pos, thenRun: ((AAMob) -> Unit)? = null): AAMob? {
        if(shouldDespawn(instance, position)) {
            return null
        }
        val entity = create()
        entity.homePosition = position.asVec()
        entity.setInstance(instance, position).thenRun {
            thenRun?.invoke(entity)
        }
        return entity
    }

    fun shouldDespawn(instance: Instance, position: Pos): Boolean {
        val despawnRadius = despawnRadius() ?: return false
        return instance.getNearbyEntities(position, despawnRadius).none { it is Player }
    }
}
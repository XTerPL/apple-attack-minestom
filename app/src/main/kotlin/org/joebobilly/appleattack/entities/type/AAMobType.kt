package org.joebobilly.appleattack.entities.type

import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityType
import net.minestom.server.instance.Instance
import org.joebobilly.appleattack.damage.AttackInfo
import org.joebobilly.appleattack.entities.mobs.AAMob
import org.joebobilly.appleattack.rewards.LootTable
import org.joebobilly.appleattack.utils.Sounds

abstract class AAMobType(id: String, startingEntityType: EntityType) : AAEntityType<AAMob>(id, startingEntityType) {
    abstract fun maxHealth(): Double
    open fun damageSound(): Sound = Sounds.GENERIC_HURT
    open fun deathSound(): Sound = Sounds.GENERIC_DEATH
    open fun meleeAttack(entity: AAMob): AttackInfo = AttackInfo.melee(1.0)

    open fun onKill(entity: AAMob): LootTable {
        return LootTable()
    }

    final override fun createUninitialized(): AAMob {
        return AAMob(this)
    }

    final override fun beforeSpawn(entity: AAMob, instance: Instance, position: Pos) {

    }
}
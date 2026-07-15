package org.joebobilly.appleattack.damage

import net.minestom.server.entity.damage.DamageType
import net.minestom.server.registry.RegistryKey

data class AttackInfo(val damage: Double, val knockback: Float = 0.4f, val damageType: RegistryKey<DamageType> = DamageType.GENERIC) {
    companion object {
        fun melee(damage: Double, knockback: Float = 0.4f): AttackInfo {
            return AttackInfo(damage, knockback, DamageType.MOB_ATTACK)
        }
    }
}
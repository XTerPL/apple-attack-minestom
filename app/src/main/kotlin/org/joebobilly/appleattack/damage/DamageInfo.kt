package org.joebobilly.appleattack.damage

import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import org.joebobilly.appleattack.mobs.AAMob
import org.joebobilly.appleattack.players.AAPlayer

class DamageInfo private constructor(val attackInfo: AttackInfo, val attacker: LivingEntity?, val source: Entity?, val attackStrength: Double) {
    companion object {
        fun of(attackInfo: AttackInfo, attacker: LivingEntity, source: Entity, attackStrength: Double = 1.0) =
            DamageInfo(attackInfo, attacker, source, attackStrength)
        fun of(attackInfo: AttackInfo, attacker: LivingEntity? = null, attackStrength: Double = 1.0) =
            DamageInfo(attackInfo, attacker, attacker, attackStrength)
    }
    fun isDirect() = attacker == source
    fun apply(entity: Entity) {
        when(entity) {
            is AAPlayer -> entity.health.dealDamage(this)
            is AAMob -> entity.health.dealDamage(this)
        }
    }
}
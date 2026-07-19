package org.joebobilly.appleattack.events

import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.entity.EntityAttackEvent
import org.joebobilly.appleattack.damage.DamageInfo
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.mobs.AAMob
import org.joebobilly.appleattack.players.AAPlayer

object DamageEvents {
    fun init(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(EntityAttackEvent::class.java) {
            when(val attacker = it.entity) {
                is AAPlayer -> playerAttack(it, attacker)
                is AAMob -> mobAttack(it, attacker)
            }
        }
    }

    private fun playerAttack(event: EntityAttackEvent, attacker: AAPlayer) {
        if(attacker.isDead) return

        val target = event.target
        if(target !is AAMob || target.isDead) return

        val attackInfo = AAItemManager.getItemProperty(attacker.itemInMainHand, ItemProperty.MELEE_ATTACK)

        target.health.dealDamage(DamageInfo.of(attackInfo, attacker))
    }

    private fun mobAttack(event: EntityAttackEvent, attacker: AAMob) {
        if(attacker.isDead) return

        val target = event.target
        if(target !is AAPlayer || target.isDead) return

        val attackInfo = attacker.type.meleeAttack(attacker)

        target.health.dealDamage(DamageInfo.of(attackInfo, attacker))
    }
}
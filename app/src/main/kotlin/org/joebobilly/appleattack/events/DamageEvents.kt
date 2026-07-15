package org.joebobilly.appleattack.events

import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.damage.AttackInfo
import org.joebobilly.appleattack.damage.DamageInfo
import org.joebobilly.appleattack.items.AAItem
import org.joebobilly.appleattack.items.AAItemManager
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
        val target = event.target
        if(target !is AAMob) return

        val weapon = attacker.itemInMainHand
        val weaponType = AAItemManager.getItem(weapon)
        val attackInfo = getMeleeAttack(weapon, weaponType)

        target.health.dealDamage(DamageInfo.of(attackInfo, attacker))
    }

    private fun mobAttack(event: EntityAttackEvent, attacker: AAMob) {
        val target = event.target
        if(target !is AAPlayer) return

        val attackInfo = attacker.type.meleeAttack(attacker)

        target.health.dealDamage(DamageInfo.of(attackInfo, attacker))
    }

    private fun <T> getMeleeAttack(weapon: ItemStack, weaponType: AAItem<T>?): AttackInfo {
        val meta = weaponType?.getMeta(weapon) ?: return AttackInfo.melee(1.0)
        return weaponType.meleeAttack(meta)
    }
}
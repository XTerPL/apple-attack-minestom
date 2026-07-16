package org.joebobilly.appleattack.events

import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.ItemEntity
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.item.PickupItemEvent
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.network.packet.server.play.CollectItemPacket
import org.joebobilly.appleattack.players.AAPlayer
import java.time.Duration
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object ItemEvents {
    fun init(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(ItemDropEvent::class.java) {
            val itemPos = it.entity.position.add(0.0, it.entity.eyeHeight - 0.3, 0.0)
            val throwPower = 0.3

            val radianConversionFactor = Math.PI / 180
            val sinPitch = sin(it.entity.position.pitch * radianConversionFactor)
            val cosPitch = cos(it.entity.position.pitch * radianConversionFactor)
            val sinYaw = sin(it.entity.position.yaw * radianConversionFactor)
            val cosYaw = cos(it.entity.position.yaw * radianConversionFactor)

            // what the hell is this about mojang
            val throwDirectionModifier = Random.nextDouble() * Math.PI * 2
            val throwDirectionModifierPower = Random.nextDouble() * 0.02

            // even more confused on this one mojang
            val verticalThrowDirectionModifier = Random.nextDouble() - Random.nextDouble()

            val velocity = Vec(
                -sinYaw * cosPitch * throwPower + cos(throwDirectionModifier) * throwDirectionModifierPower,
                -sinPitch * throwPower + 0.1 + verticalThrowDirectionModifier * 0.1,
                cosYaw * cosPitch * throwPower + sin(throwDirectionModifier) * throwDirectionModifierPower,
            ).mul(20.0)

            val entity = ItemEntity(it.itemStack)
            entity.setPickupDelay(Duration.ofSeconds(2))
            entity.setInstance(it.player.instance, itemPos).thenRun {
                entity.velocity = velocity
            }
        }
        eventHandler.addListener(PickupItemEvent::class.java) {
            if(it.isCancelled) {
                return@addListener
            }
            val collector = it.livingEntity
            if(collector !is AAPlayer) {
                it.isCancelled = true
                return@addListener
            }
            it.isCancelled = collectReward(collector, it.itemEntity)
        }
    }

    private fun collectReward(player: AAPlayer, item: ItemEntity): Boolean {
        val remainder = player.inventory.addItemStack(item.itemStack, TransactionOption.ALL)
        if(remainder.isAir) return false
        val collected = item.itemStack.amount() - remainder.amount()
        if(collected > 0) {
            item.sendPacketToViewersAndSelf(
                CollectItemPacket(item.entityId, player.entityId, collected
            ))
        }
        item.itemStack = remainder
        return true
    }
}
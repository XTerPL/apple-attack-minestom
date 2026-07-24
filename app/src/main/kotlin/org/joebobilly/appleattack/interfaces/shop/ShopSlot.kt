package org.joebobilly.appleattack.interfaces.shop

import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.interfaces.ButtonClick
import org.joebobilly.appleattack.interfaces.Slot
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.rewards.Transaction
import org.joebobilly.appleattack.utils.Sounds

class ShopSlot(
    val transaction: Transaction,
    val purchaseSound: Sound = Sounds.GENERIC_PURCHASE,
    val refuseSound: Sound = Sounds.GENERIC_REFUSE
) : Slot.Button() {
    override fun onClick(player: Player, click: ButtonClick) {
        if(player !is AAPlayer) return
        if(click !is ButtonClick.Left) return
        if(transaction.apply(player)) {
            if(click.modified) {
                while(true) {
                    if(transaction.apply(player)) break
                }
            }
            player.playSound(purchaseSound)
        }
        else {
            player.playSound(refuseSound)
        }
    }

    override fun getIcon(): ItemStack {
        return transaction.cost.addCostLore(transaction.reward.getIcon(), true)
    }
}
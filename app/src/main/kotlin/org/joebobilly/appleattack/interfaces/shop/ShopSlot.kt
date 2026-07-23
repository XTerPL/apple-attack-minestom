package org.joebobilly.appleattack.interfaces.shop

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.interfaces.ButtonClick
import org.joebobilly.appleattack.interfaces.Slot
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.rewards.Transaction
import org.joebobilly.appleattack.utils.InventoryUtils
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
        val itemStack = transaction.reward.getIcon()

        val lore = itemStack.get(DataComponents.LORE)
        val newLore = mutableListOf<Component>()
        newLore.addAll(transaction.cost.getLore())
        if(!lore.isNullOrEmpty()) {
            newLore.add(Component.text("│", NamedTextColor.DARK_GRAY))
            newLore.addAll(lore)
        }
        return itemStack.withLore(InventoryUtils.sanitizeLore(newLore))
    }
}
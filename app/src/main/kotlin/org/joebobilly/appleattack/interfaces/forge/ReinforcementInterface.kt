package org.joebobilly.appleattack.interfaces.forge

import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.interfaces.Slot
import org.joebobilly.appleattack.interfaces.UserInterface
import org.joebobilly.appleattack.items.AAItem
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.items.tools.reinforcement.Reinforcement
import org.joebobilly.appleattack.rewards.Cost
import org.joebobilly.appleattack.utils.AreaUtils
import org.joebobilly.appleattack.utils.InventoryUtils
import org.joebobilly.appleattack.utils.Sounds

class ReinforcementInterface(val reinforcement: Reinforcement)
    : UserInterface(InventoryType.CHEST_5_ROW, reinforcement.inventoryName()) {
    companion object {
        private val highlightRectangle = Pair(Pair(1, 1), Pair(3, 3))
        private val highlightRectangle2 = Pair(Pair(5, 1), Pair(7, 3))
        private val reinforcementIconPoint = Pair(4, 2)
    }

    private val toolSlot = Slot.Input(InventoryUtils.icon(
        Material.WHITE_STAINED_GLASS_PANE, "Tool/Armor Slot",
        listOf("Put tools and armor here!", "The mechanical limbs of the player.")
    )) {
        itemStack -> AAItemManager.getItemMetaPair(itemStack)?.let { tryReinforce(it) } != null
    }
    private val resultSlot = object : Slot.Output() {
        override fun getResult(): ItemStack {
            return AAItemManager.getItemMetaPair(toolSlot.itemStack)?.let { tryReinforce(it) }?.first?.create()
                ?: ItemStack.AIR
        }

        override fun getCost(): Cost {
            return AAItemManager.getItemMetaPair(toolSlot.itemStack)?.let { tryReinforce(it) }?.second ?: Cost.free()
        }

        override fun onSuccess(player: Player) {
            toolSlot.itemStack = ItemStack.AIR
        }

        override fun onOverallSuccess(player: Player) {
            player.playSound(Sounds.REINFORCE_SUCCEED)
        }

        override fun canSwapHands(player: Player): Boolean {
            return true
        }

        override fun onSuccessfulSwapHands(result: ItemStack, player: Player) {
            toolSlot.itemStack = result
        }
    }

    override fun defineSlots(definer: SlotDefiner) {
        definer.setSlot(2, 2, toolSlot)
        definer.setSlot(6, 2, resultSlot)
    }

    override fun getBackgroundIcon(x: Int, y: Int): ItemStack {
        val point = Pair(x, y)
        val backgroundIcon = super.getBackgroundIcon(x, y)
        if(AreaUtils.withinRectangle(point, highlightRectangle))
            return backgroundIcon.withMaterial(Material.BLACK_STAINED_GLASS_PANE)
        if(AreaUtils.withinRectangle(point, highlightRectangle2))
            return backgroundIcon.withMaterial(Material.BLACK_STAINED_GLASS_PANE)
        if(AreaUtils.exactPoint(point, reinforcementIconPoint))
            return reinforcement.icon()

        return backgroundIcon
    }

    private fun <METATYPE> tryReinforce(itemMetaPair: AAItemMetaPair<METATYPE>): Pair<AAItemMetaPair<*>, Cost>? {
        val meta = itemMetaPair.meta
        if(meta is ToolMeta) {
            @Suppress("UNCHECKED_CAST")
            return reinforcement.reinforce(AAItemMetaPair(
                itemMetaPair.itemType as AAItem<ToolMeta>, meta
            ))
        }

        return null
    }
}
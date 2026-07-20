package org.joebobilly.appleattack.interfaces.forge

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.interfaces.Slot
import org.joebobilly.appleattack.interfaces.UserInterface
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ForgeMaterialType
import org.joebobilly.appleattack.items.tools.type.ToolType
import org.joebobilly.appleattack.utils.AreaUtils
import org.joebobilly.appleattack.utils.Sounds

abstract class ForgeInterface(toolType: ToolType) : UserInterface(InventoryType.CHEST_5_ROW, Component.text("Forge of Creation: ${toolType.displayName}")) {
    companion object {
        fun forgeMaterialSlot(type: ForgeMaterialType): Slot.Input {
            return Slot.Input(type.getSlotCover()) { item ->
                AAItemManager.withItemProperty(item, ItemProperty.FORGE_MATERIAL) {
                    it.getType() == type
                } ?: false
            }
        }

        private val gridRectangle = Pair(Pair(1, 1), Pair(3, 3))
        private val highlightRectangle = Pair(Pair(0, 0), Pair(4, 4))
        private val highlightRectangle2 = Pair(Pair(6, 1), Pair(8, 3))
        private val highlightConnector = Pair(5, 2)
    }

    private val resultSlot = object : Slot.Output() {
        override fun getResult(): ItemStack {
            val result = getResultingTool() ?: return ItemStack.AIR
            return result.create()
        }

        override fun onSuccess(player: Player) {
            clearRecipe()
        }

        override fun onOverallSuccess(player: Player) {
            player.playSound(Sounds.FORGE_SUCCEED)
        }
    }

    override fun defineSlots(definer: SlotDefiner) {
        definer.setSlot(7, 2, resultSlot)
    }

    final override fun getBackgroundIcon(x: Int, y: Int): ItemStack {
        val point = Pair(x, y)
        if(AreaUtils.withinRectangle(point, gridRectangle)) return ItemStack.AIR
        val backgroundIcon = super.getBackgroundIcon(x, y)
        if(AreaUtils.withinRectangle(point, highlightRectangle))
            return backgroundIcon.withMaterial(Material.BLACK_STAINED_GLASS_PANE)
        if(AreaUtils.withinRectangle(point, highlightRectangle2))
            return backgroundIcon.withMaterial(Material.BLACK_STAINED_GLASS_PANE)
        if(highlightConnector.first == x && highlightConnector.second == y)
            return backgroundIcon.withMaterial(Material.BLACK_STAINED_GLASS_PANE)

        return backgroundIcon
    }

    protected abstract fun getResultingTool(): AAItemMetaPair<*>?
    protected abstract fun clearRecipe()
}
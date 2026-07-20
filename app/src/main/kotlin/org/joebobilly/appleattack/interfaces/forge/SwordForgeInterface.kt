package org.joebobilly.appleattack.interfaces.forge

import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.tools.ForgeMaterialType
import org.joebobilly.appleattack.items.tools.ForgedToolMeta
import org.joebobilly.appleattack.items.tools.type.SwordItem
import org.joebobilly.appleattack.items.tools.type.ToolType

class SwordForgeInterface : ForgeInterface(ToolType.SWORD) {
    private val bladeUpSlot = forgeMaterialSlot(ForgeMaterialType.ATTACK)
    private val bladeDownSlot = forgeMaterialSlot(ForgeMaterialType.ATTACK)
    private val handleSlot = forgeMaterialSlot(ForgeMaterialType.HANDLE)

    override fun defineSlots(definer: SlotDefiner) {
        super.defineSlots(definer)
        definer.setSlot(2, 1, bladeUpSlot)
        definer.setSlot(2, 2, bladeDownSlot)
        definer.setSlot(2, 3, handleSlot)
    }
    override fun getResultingTool(): AAItemMetaPair<*>? {
        return AAItemMetaPair(SwordItem.Forged, ForgedToolMeta(
            SwordItem.Recipe(
                handleSlot.getItemMetaPair() ?: return null,
                bladeDownSlot.getItemMetaPair() ?: return null,
                bladeUpSlot.getItemMetaPair() ?: return null
            )
        ))
    }
    override fun clearRecipe() {
        bladeUpSlot.itemStack = ItemStack.AIR
        bladeDownSlot.itemStack = ItemStack.AIR
        handleSlot.itemStack = ItemStack.AIR
    }
}
package org.joebobilly.appleattack.interfaces.forge

import net.kyori.adventure.text.Component
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.interfaces.Slot
import org.joebobilly.appleattack.interfaces.UserInterface
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.LoreProvider
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.items.tools.type.ToolItem
import org.joebobilly.appleattack.utils.AreaUtils
import org.joebobilly.appleattack.utils.Sounds

class StationInterface : UserInterface(InventoryType.CHEST_5_ROW, "Station of Upgrading") {
    companion object {
        private fun slotCover(material: Material, name: String, description: List<String>): ItemStack {
            return ItemStack.builder(material).set(DataComponents.ITEM_NAME, Component.text(name))
                .lore(LoreProvider.formatDescription(description)).build()
        }

        private val highlightRectangle = Pair(Pair(0, 1), Pair(2, 3))
        private val highlightRectangle2 = Pair(Pair(6, 1), Pair(8, 3))
        private val highlightConnector = Pair(Pair(3, 2), Pair(5, 2))
    }

    private val toolSlot = Slot.Input(slotCover(
        Material.WHITE_STAINED_GLASS_PANE, "Tool/Armor Slot",
        listOf("Put tools and armor here!", "The mechanical limbs of the player.")
    )) { AAItemManager.getItem(it) is ToolItem<*> }
    private val upgradeSlot = Slot.Storage(slotCover(
        Material.LIGHT_BLUE_STAINED_GLASS_PANE, "Upgrade Slot",
        listOf("Put upgrades here!", "Can also be called modifier items.")
    )) { AAItemManager.hasItemProperty(it, ItemProperty.FORGE_UPGRADE_DATA) }
    private val resultSlot = object : Slot.Output() {
        override fun getResult(): ItemStack {
            val toolType = AAItemManager.getItem(toolSlot.itemStack)
            if(toolType is ToolItem<*>) {
                return upgradeTool(toolType)
            }
            return ItemStack.AIR
        }

        private fun <T : ToolMeta> upgradeTool(toolType: ToolItem<T>): ItemStack {
            val meta = toolType.getMeta(toolSlot.itemStack) ?: return ItemStack.AIR
            if(!toolType.safelyAddUpgradeToMeta(meta, upgradeSlot.getItemMetaPair() ?: return ItemStack.AIR)) {
                return ItemStack.AIR
            }
            return toolType.create(meta)
        }

        override fun onSuccess(player: Player) {
            toolSlot.itemStack = ItemStack.AIR
            upgradeSlot.takeOne()
        }

        override fun onOverallSuccess(player: Player) {
            player.playSound(Sounds.UPGRADE_SUCCEED)
        }

        override fun canSwapHands(player: Player): Boolean {
            return true
        }

        override fun onSuccessfulSwapHands(result: ItemStack, player: Player) {
            toolSlot.itemStack = result
        }
    }

    override fun defineSlots(definer: SlotDefiner) {
        definer.setSlot(1, 2, toolSlot)
        definer.setSlot(4, 2, upgradeSlot)
        definer.setSlot(7, 2, resultSlot)
    }

    override fun getBackgroundIcon(x: Int, y: Int): ItemStack {
        val point = Pair(x, y)
        val backgroundIcon = super.getBackgroundIcon(x, y)
        if(AreaUtils.withinRectangle(point, highlightRectangle))
            return backgroundIcon.withMaterial(Material.BLACK_STAINED_GLASS_PANE)
        if(AreaUtils.withinRectangle(point, highlightRectangle2))
            return backgroundIcon.withMaterial(Material.BLACK_STAINED_GLASS_PANE)
        if(AreaUtils.withinRectangle(point, highlightConnector))
            return backgroundIcon.withMaterial(Material.BLACK_STAINED_GLASS_PANE)

        return backgroundIcon
    }
}
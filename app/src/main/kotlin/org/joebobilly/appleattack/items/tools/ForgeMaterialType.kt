package org.joebobilly.appleattack.items.tools

import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.utils.InventoryUtils

enum class ForgeMaterialType(val itemTypeName: String, val slotCoverMaterial: Material, val description: List<String>) {
    HANDLE("Handle", Material.BROWN_STAINED_GLASS_PANE,
        listOf("Handles are essential for every tool.")),
    ATTACK("Attack Material", Material.RED_STAINED_GLASS_PANE,
        listOf("These materials are what deals your damage!"));

    fun getSlotCover(): ItemStack {
        val lore = mutableListOf("Put ${itemTypeName.lowercase()}s here!")
        lore.addAll(description)
        return InventoryUtils.icon(slotCoverMaterial, "$itemTypeName Slot", lore)
    }
}
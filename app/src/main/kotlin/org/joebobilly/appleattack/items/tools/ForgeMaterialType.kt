package org.joebobilly.appleattack.items.tools

import net.kyori.adventure.text.Component
import net.minestom.server.component.DataComponents
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.LoreProvider

enum class ForgeMaterialType(val itemTypeName: String, val slotCoverMaterial: Material, val description: List<String>) {
    HANDLE("Handle", Material.BROWN_STAINED_GLASS_PANE,
        listOf("Handles are essential for every tool.")),
    ATTACK("Attack Material", Material.RED_STAINED_GLASS_PANE,
        listOf("These materials are what deals your damage!"));

    fun getSlotCover(): ItemStack {
        val lore = mutableListOf("Put ${itemTypeName.lowercase()}s here!")
        lore.addAll(description)
        return ItemStack.builder(slotCoverMaterial).set(DataComponents.ITEM_NAME, Component.text("$itemTypeName Slot"))
            .lore(LoreProvider.formatDescription(lore)).build()
    }
}
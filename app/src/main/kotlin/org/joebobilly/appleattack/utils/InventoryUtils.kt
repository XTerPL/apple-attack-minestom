package org.joebobilly.appleattack.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.LoreProvider

object InventoryUtils {
    fun getDimensions(inventoryType: InventoryType): Pair<Int, Int>? {
        return when(inventoryType) {
            InventoryType.CHEST_1_ROW -> Pair(9, 1)
            InventoryType.CHEST_2_ROW -> Pair(9, 2)
            InventoryType.CHEST_3_ROW -> Pair(9, 3)
            InventoryType.CHEST_4_ROW -> Pair(9, 4)
            InventoryType.CHEST_5_ROW -> Pair(9, 5)
            InventoryType.CHEST_6_ROW -> Pair(9, 6)
            InventoryType.WINDOW_3X3 -> Pair(3, 3)
            InventoryType.HOPPER -> Pair(5, 1)
            else -> null
        }
    }
    fun addLikeShiftClick(itemStack: ItemStack, player: Player): ItemStack {
        var itemStack = itemStack
        val maxCount = itemStack.maxStackSize()
        for(j in 0..3) {
            for(i in 8 downTo 0) {
                val slot = i + j * 9
                val slotItemStack = player.inventory.getItemStack(slot)
                if(slotItemStack.isSimilar(itemStack)) {
                    val count = slotItemStack.amount() + itemStack.amount()
                    val slotCount = count.coerceAtMost(maxCount)
                    val shiftedAmount = count - slotCount
                    player.inventory.setItemStack(slot, slotItemStack.withAmount(slotCount))
                    if(shiftedAmount <= 0) return ItemStack.AIR
                    itemStack = itemStack.withAmount(shiftedAmount)
                }
            }
        }
        for(j in 0..3) {
            for(i in 8 downTo 0) {
                val slot = i + j * 9
                val slotItemStack = player.inventory.getItemStack(slot)
                if(slotItemStack.isAir) {
                    player.inventory.setItemStack(slot, itemStack)
                    return ItemStack.AIR
                }
            }
        }
        return itemStack
    }
    fun takeUsingMap(player: Player, takenItemMap: Map<Int, Int>) {
        for(takenEntry in takenItemMap) {
            val slot = takenEntry.key
            val amount = takenEntry.value
            val itemStack = player.inventory.getItemStack(slot)
            player.inventory.setItemStack(slot, itemStack.consume(amount))
        }
    }
    fun sanitizeLore(lore: List<Component>): List<Component> {
        if(lore.size > 256) {
            val newLore = mutableListOf<Component>()
            for(i in 0..<255) {
                newLore.add(lore[i])
            }
            newLore.add(Component.text("-- lore cutoff --", NamedTextColor.DARK_GRAY))
            return sanitizeLore(newLore)
        }
        return lore.map { line -> line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE) }
    }
    fun icon(material: Material, name: Component, description: List<String>): ItemStack {
        return ItemStack.builder(material).set(DataComponents.ITEM_NAME, name)
            .lore(LoreProvider.formatDescription(description)).build()
    }
    fun icon(material: Material, name: String, description: List<String>): ItemStack {
        return icon(material, Component.text(name), description)
    }
}
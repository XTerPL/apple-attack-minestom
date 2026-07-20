package org.joebobilly.appleattack.utils

import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack

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
}
package org.joebobilly.appleattack.interfaces

import com.google.common.base.Suppliers
import net.kyori.adventure.text.Component
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.TooltipDisplay
import org.joebobilly.appleattack.utils.InventoryUtils

abstract class UserInterface(val inventoryType: InventoryType, val title: Component) {
    class SlotDefiner internal constructor(val inventoryType: InventoryType, val dimensions: Pair<Int, Int>) {
        private val slots = mutableMapOf<Int, Slot>()

        fun setSlot(slotId: Int, slot: Slot): SlotDefiner {
            check(slotId in 0..<inventoryType.size) {
                "Slot ID out of range 0 - ${inventoryType.size - 1}"
            }
            slots[slotId] = slot
            return this
        }
        fun setSlot(x: Int, y: Int, slot: Slot): SlotDefiner {
            check(x in 0..<dimensions.first) { "Slot X out of range 0 - ${dimensions.first - 1}" }
            check(y in 0..<dimensions.second) { "Slot Y out of range 0 - ${dimensions.second - 1}" }
            return setSlot(x + y * dimensions.first, slot)
        }

        internal fun getSlots(): Map<Int, Slot> {
            return slots.toMap()
        }
    }

    val dimensions = InventoryUtils.getDimensions(inventoryType)
        ?: throw IllegalArgumentException("Unsupported Inventory Type: $inventoryType")
    private val _inventory = Suppliers.memoize { UserInterfaceInventory(this) }
    val inventory: UserInterfaceInventory
        get() = _inventory.get()

    abstract fun defineSlots(definer: SlotDefiner)
    open fun onUpdate() {

    }
    open fun getBackgroundIcon(x: Int, y: Int): ItemStack {
        return ItemStack.builder(Material.GRAY_STAINED_GLASS_PANE)
            .set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.EMPTY.withHideTooltip(true)).build()
    }
    open fun onClose(player: Player) {
        val items = mutableListOf<ItemStack>()
        for(entry in inventory.slots) {
            val slot = entry.value
            if(slot is Slot.Usable) {
                items.add(slot.itemStack)
            }
        }
        player.inventory.addItemStacks(items, TransactionOption.ALL).forEach {
            if(!it.isAir) player.dropItem(it)
        }
    }
}
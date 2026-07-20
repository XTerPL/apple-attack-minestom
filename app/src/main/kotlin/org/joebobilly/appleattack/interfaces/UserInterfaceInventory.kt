package org.joebobilly.appleattack.interfaces

import net.minestom.server.inventory.Inventory
import org.joebobilly.appleattack.interfaces.UserInterface.SlotDefiner

class UserInterfaceInventory internal constructor(val userInterface: UserInterface) : Inventory(userInterface.inventoryType, userInterface.title) {
    val slots: Map<Int, Slot>

    init {
        val definer = SlotDefiner(inventoryType, userInterface.dimensions)
        userInterface.defineSlots(definer)
        slots = definer.getSlots()
        updateContents()
    }

    fun updateContents() {
        userInterface.onUpdate()
        for(i in 0..<size) {
            setItemStack(i, slots[i]?.getIcon() ?: userInterface.getBackgroundIcon(
                i % userInterface.dimensions.first, i / userInterface.dimensions.first)
            )
        }
    }
}
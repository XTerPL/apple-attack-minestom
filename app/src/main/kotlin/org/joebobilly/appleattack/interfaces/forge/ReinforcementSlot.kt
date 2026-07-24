package org.joebobilly.appleattack.interfaces.forge

import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.interfaces.ButtonClick
import org.joebobilly.appleattack.interfaces.Slot
import org.joebobilly.appleattack.items.tools.reinforcement.Reinforcement

class ReinforcementSlot(val reinforcement: Reinforcement) : Slot.Button() {
    override fun onClick(player: Player, click: ButtonClick) {
        player.openInventory(ReinforcementInterface(reinforcement).inventory)
    }

    override fun getIcon(): ItemStack {
        return reinforcement.icon()
    }
}
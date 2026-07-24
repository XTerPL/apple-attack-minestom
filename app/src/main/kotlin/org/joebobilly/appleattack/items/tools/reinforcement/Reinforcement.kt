package org.joebobilly.appleattack.items.tools.reinforcement

import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.rewards.Cost

interface Reinforcement {
    fun inventoryName() = "Shop of Reinforcement"
    fun icon(): ItemStack
    fun reinforce(itemMetaPair: AAItemMetaPair<ToolMeta>): Pair<AAItemMetaPair<*>, Cost>?
}
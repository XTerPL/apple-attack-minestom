package org.joebobilly.appleattack.items.tools.reinforcement

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.rewards.Cost
import org.joebobilly.appleattack.utils.InventoryUtils

object ClearAllUpgradesReinforcement : Reinforcement {
    override fun icon(): ItemStack {
        return InventoryUtils.icon(Material.BRUSH,
            Component.text("Clear ALL Upgrades", TextColor.color(0xF99780)),
            listOf(
                "There is no coming back from this.",
                "Upgrades aren't given back.",
                "You have been warned."
            )
        )
    }

    override fun reinforce(itemMetaPair: AAItemMetaPair<ToolMeta>): Pair<AAItemMetaPair<*>, Cost> {
        val meta = itemMetaPair.meta.apply { clearUpgrades() }
        return Pair(AAItemMetaPair(itemMetaPair.itemType, meta), Cost.free())
    }
}
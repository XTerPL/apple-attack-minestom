package org.joebobilly.appleattack.content.items.tools.swords

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.items.tools.ToolStat
import org.joebobilly.appleattack.items.tools.type.SwordItem
import org.joebobilly.appleattack.items.tools.type.ToolDefinition
import org.joebobilly.appleattack.items.icons.ItemIcon

object StarterSword : SwordItem.Defined("starter_sword") {
    init {
        ItemProperty.ICON.set { ItemIcon(Material.WOODEN_SWORD) }
        ItemProperty.DESCRIPTION.set {
            listOf("Your first weapon.")
        }
    }
    override fun defaultName() = Component.text("Starter Sword", NamedTextColor.GOLD)

    override fun defineTool(
        meta: ToolMeta,
        builder: ToolDefinition.Builder
    ) {
        builder.addStat(ToolStat.ATTACK, 3.0)
        builder.addStat(ToolStat.MODIFIER_SLOTS, 1)
    }
}
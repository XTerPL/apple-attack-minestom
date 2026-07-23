package org.joebobilly.appleattack.content.items.upgrades

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.Material
import org.joebobilly.appleattack.content.traits.Traits
import org.joebobilly.appleattack.items.AARarity
import org.joebobilly.appleattack.items.BasicAAItem
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ForgeUpgradeData
import org.joebobilly.appleattack.items.icons.ItemIcon

object CreativeModifier : BasicAAItem("creative_modifier", backingMaterial = Material.GOLD_INGOT) {
    init {
        ItemProperty.DESCRIPTION.set {
            listOf("Gives an extra modifier, infinitely.")
        }
        ItemProperty.ICON.set {
            ItemIcon(Material.GOLD_INGOT)
        }
        ItemProperty.GLOW.set { true }
        ItemProperty.RARITY.set { AARarity.CREATIVE }
        ItemProperty.FORGE_UPGRADE_DATA.set {
            ForgeUpgradeData(Traits.CREATIVE, -1)
        }
    }
    override fun defaultName() = Component.text("Creative Modifier", NamedTextColor.YELLOW)
}
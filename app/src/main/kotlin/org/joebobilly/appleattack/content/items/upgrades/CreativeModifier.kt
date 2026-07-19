package org.joebobilly.appleattack.content.items.upgrades

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.Material
import org.joebobilly.appleattack.content.traits.Traits
import org.joebobilly.appleattack.items.AARarity
import org.joebobilly.appleattack.items.BasicAAItem
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ForgeUpgradeData

object CreativeModifier : BasicAAItem("creative_modifier", backingMaterial = Material.GOLD_INGOT) {
    init {
        ItemProperty.NAME.set {
            Component.text("Creative Modifier", NamedTextColor.YELLOW)
        }
        ItemProperty.DESCRIPTION.set {
            listOf("Gives an extra modifier, infinitely.")
        }
        ItemProperty.GLOW.set { true }
        ItemProperty.RARITY.set { AARarity.CREATIVE }
        ItemProperty.FORGE_UPGRADE_DATA.set {
            ForgeUpgradeData(Traits.CREATIVE, -1)
        }
    }
}
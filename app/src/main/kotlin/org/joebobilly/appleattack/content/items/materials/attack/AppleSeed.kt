package org.joebobilly.appleattack.content.items.materials.attack

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.BasicAAItem
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ForgeMaterial

object AppleSeed : BasicAAItem("apple_seed", backingMaterial = Material.INK_SAC) {
    init {
        ItemProperty.NAME.set {
            Component.text("Apple Seed", NamedTextColor.DARK_GRAY)
        }
        ItemProperty.DESCRIPTION.set {
            listOf("And you're gonna use it", "to make a weapon, sure.")
        }
        ItemProperty.FORGE_MATERIAL.set {
            ForgeMaterial.Attack(3.0, 1, emptyMap())
        }
    }
}
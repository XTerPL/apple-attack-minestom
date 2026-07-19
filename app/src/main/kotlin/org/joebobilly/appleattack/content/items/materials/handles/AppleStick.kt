package org.joebobilly.appleattack.content.items.materials.handles

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.BasicAAItem
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ForgeMaterial

object AppleStick : BasicAAItem("apple_stick", backingMaterial = Material.STICK) {
    init {
        ItemProperty.NAME.set {
            Component.text("Apple Stick", NamedTextColor.GOLD)
        }
        ItemProperty.DESCRIPTION.set {
            listOf("The stem of an apple, I guess?")
        }
        ItemProperty.FORGE_MATERIAL.set {
            ForgeMaterial.Handle(0, emptyMap())
        }
    }
}
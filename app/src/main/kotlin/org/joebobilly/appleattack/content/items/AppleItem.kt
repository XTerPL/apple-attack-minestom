package org.joebobilly.appleattack.content.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.BasicAAItem
import org.joebobilly.appleattack.items.ItemProperty

object AppleItem : BasicAAItem("apple", backingMaterial = Material.APPLE) {
    init {
        ItemProperty.NAME.set {
            Component.text("Apple", NamedTextColor.RED)
        }
        ItemProperty.DESCRIPTION.set {
            listOf("An apple.")
        }
    }
}
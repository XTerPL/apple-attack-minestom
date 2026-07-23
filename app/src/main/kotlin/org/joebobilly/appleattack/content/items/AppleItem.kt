package org.joebobilly.appleattack.content.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.BasicAAItem
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.icons.PlayerHeadIcon

object AppleItem : BasicAAItem("apple", backingMaterial = Material.APPLE) {
    init {
        ItemProperty.DESCRIPTION.set {
            listOf("An apple.")
        }
        ItemProperty.ICON.set {
            PlayerHeadIcon(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJiMzViZGE1ZWJkZjEzNWY0ZTcxY2U0OTcyNmZiZWM1NzM5ZjBhZGVkZjAxYzUxOWUyYWVhN2Y1MTk1MWVhMiJ9fX0="
            )
        }
    }
    override fun defaultName() = Component.text("Apple", NamedTextColor.RED)
}
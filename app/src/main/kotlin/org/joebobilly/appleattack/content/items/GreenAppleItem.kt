package org.joebobilly.appleattack.content.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.BasicAAItem
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.icons.PlayerHeadIcon

object GreenAppleItem : BasicAAItem("green_apple", backingMaterial = Material.APPLE) {
    init {
        ItemProperty.NAME.set {
            Component.text("Apple", NamedTextColor.GREEN)
        }
        ItemProperty.DESCRIPTION.set {
            listOf("An apple.")
        }
        ItemProperty.ICON.set {
            PlayerHeadIcon(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU2NDc5N2NkNjI2NjQ0NDhlZDAyOGU0ODdhY2Q5NWQ1NzA3NWRjZTQ5YTM1NmZjYzY1NjU1YjJiNTI1ZGRiIn19fQ=="
            )
        }
    }
}
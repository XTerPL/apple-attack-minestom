package org.joebobilly.appleattack.content.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.BasicAAItem

object GreenAppleItem : BasicAAItem("green_apple", backingMaterial = Material.APPLE) {
    override fun name(meta: Unit): Component {
        return Component.text("Apple", NamedTextColor.GREEN)
    }

    override fun description(meta: Unit): List<String> {
        return listOf("An apple.")
    }
}
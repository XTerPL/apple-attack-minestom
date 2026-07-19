package org.joebobilly.appleattack.content.traits

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.joebobilly.appleattack.items.tools.traits.Trait
import org.joebobilly.appleattack.items.tools.traits.TraitManager

object Traits {
    val CREATIVE = Trait("creative", Int.MAX_VALUE, Int.MAX_VALUE, "✎ Creative",
        Style.style(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD, TextDecoration.ITALIC))

    fun register() {
        TraitManager.register(CREATIVE)
    }
}
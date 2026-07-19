package org.joebobilly.appleattack.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration

enum class AARarity(private val _displayName: String, private val style: Style) {
    COMMON("COMMON", Style.style(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)),
    UNCOMMON("UNCOMMON", Style.style(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)),
    RARE("RARE", Style.style(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)),
    EPIC("EPIC", Style.style(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD)),
    LEGENDARY("LEGENDARY", Style.style(NamedTextColor.GOLD).decorate(TextDecoration.BOLD)),
    FORGED("FORGED", Style.style(NamedTextColor.GOLD).decorate(TextDecoration.ITALIC)),
    CREATIVE("CREATIVE", Style.style(NamedTextColor.YELLOW)
        .decorate(TextDecoration.BOLD, TextDecoration.ITALIC)),
    ;

    val displayName: Component get() = Component.text(_displayName, style)

    fun wrapType(typeName: String): Component {
        if(typeName.isBlank()) return displayName
        return Component.text("$_displayName ${typeName.uppercase()}", style)
    }
}
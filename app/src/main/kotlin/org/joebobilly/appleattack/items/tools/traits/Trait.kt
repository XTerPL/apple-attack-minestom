package org.joebobilly.appleattack.items.tools.traits

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import org.joebobilly.appleattack.utils.NumberUtils

open class Trait(val id: String, val maxLevel: Int = 1, val value: Int = 1, val name: String, val nameStyle: Style) {
    companion object {
        val comparator: Comparator<Map.Entry<Trait, Int>> =
            Comparator.comparing<Map.Entry<Trait, Int>, _> { it.key.value }.thenComparing { it.key.id }

        fun getTraitLore(traits: Map<Trait, Int>): List<Component> {
            return traits.entries.sortedWith(comparator).map { it.key.traitDisplay(it.value) }
        }
    }

    init {
        require(maxLevel >= 1) { "maxLevel must be >= 1, got $maxLevel" }
        TraitManager.throwIfFrozen { "Cannot create trait $id after server startup (did you forget to register this trait?)" }
    }

    fun traitDisplay(level: Int?): Component {
        if(level == null) {
            return Component.text(name, nameStyle)
        }

        var traitLabel = name
        if(level != 1) {
            traitLabel += " " + NumberUtils.toRoman(level)
        }
        return Component.text(traitLabel, nameStyle)
    }
}
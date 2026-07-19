package org.joebobilly.appleattack.items.tools

import net.kyori.adventure.text.Component
import org.joebobilly.appleattack.items.LoreProvider
import org.joebobilly.appleattack.items.tools.traits.Trait

data class ForgeUpgradeData(val modifierCost: Int, val traitsAdded: Map<Trait, Int>) : LoreProvider {
    constructor(trait: Trait, modifierCost: Int = 1) : this(modifierCost, mapOf(Pair(trait, 1)))

    override fun getLore(meta: Any?): List<Component> {
        return Trait.getTraitLore(traitsAdded)
    }
}
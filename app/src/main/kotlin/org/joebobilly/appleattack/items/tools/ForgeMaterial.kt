package org.joebobilly.appleattack.items.tools

import net.kyori.adventure.text.Component
import org.joebobilly.appleattack.items.ItemTypeNameProvider
import org.joebobilly.appleattack.items.LoreProvider
import org.joebobilly.appleattack.items.tools.traits.Trait

sealed class ForgeMaterial(private val modifierSlots: Int, val traitsAdded: Map<Trait, Int>) : LoreProvider, ItemTypeNameProvider {
    class Handle(modifierSlots: Int, traitsAdded: Map<Trait, Int>) : ForgeMaterial(modifierSlots, traitsAdded) {
        override fun getExtraStats(): List<ToolStat.StatEntry<*>> {
            return emptyList()
        }
        override fun getItemTypeName(meta: Any?): String {
            return "handle"
        }
    }
    class Attack(private val attack: Double, modifierSlots: Int, traitsAdded: Map<Trait, Int>) : ForgeMaterial(modifierSlots, traitsAdded) {
        override fun getExtraStats(): List<ToolStat.StatEntry<*>> {
            return listOf(ToolStat.StatEntry(ToolStat.ATTACK, attack))
        }
        override fun getItemTypeName(meta: Any?): String {
            return "attack"
        }
    }

    fun getStats(): List<ToolStat.StatEntry<*>> {
        val list = mutableListOf<ToolStat.StatEntry<*>>(ToolStat.StatEntry(ToolStat.MODIFIER_SLOTS, modifierSlots))
        list.addAll(getExtraStats())
        return list.toList()
    }
    protected abstract fun getExtraStats(): List<ToolStat.StatEntry<*>>

    override fun getLore(meta: Any?): List<Component> {
        val statLine = ToolStat.combineStatDisplays(ToolStat.getStatDisplays(getStats()))
        val lore = mutableListOf(statLine)
        lore.addAll(Trait.getTraitLore(traitsAdded))
        return lore
    }
}
package org.joebobilly.appleattack.items.tools

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.joebobilly.appleattack.items.ItemTypeNameProvider
import org.joebobilly.appleattack.items.LoreProvider
import org.joebobilly.appleattack.items.tools.traits.Trait
import org.joebobilly.appleattack.items.tools.type.ToolDefinition
import org.joebobilly.appleattack.items.tools.type.ToolType

class ToolData(definition: ToolDefinition, upgrades: List<ForgeUpgradeData>) : LoreProvider, ItemTypeNameProvider {
    data class Provider(val definition: ToolDefinition, val toolMeta: ToolMeta) {
        fun getToolData(): ToolData {
            return ToolData(definition, toolMeta.upgrades)
        }
    }

    val toolType: ToolType = definition.toolType
    private val stats: Map<ToolStat<*>, ToolStat.StatEntry<*>> = definition.stats
    private val traits: Map<Trait, Int>
    val usedModifiers: Int

    init {
        var traits = definition.baseTraits.toMutableMap()
        var usedModifiers = 0

        for(upgrade in upgrades) {
            usedModifiers += upgrade.modifierCost
            upgrade.traitsAdded.forEach {
                val newLevel = (traits[it.key] ?: 0) + it.value
                if(newLevel == 0) {
                    traits.remove(it.key)
                }
                else {
                    traits[it.key] = newLevel
                }
            }
        }

        this.traits = traits.toMap()
        this.usedModifiers = usedModifiers
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getStat(stat: ToolStat<T>): T {
        val entry = stats[stat] ?: throw NoSuchElementException("No stat ${stat.displayName} in tool")
        return (entry as ToolStat.StatEntry<T>).value
    }

    fun getPresentTraits(): Set<Trait> {
        return traits.keys
    }
    fun getTraitLevel(trait: Trait): Int {
        return traits[trait] ?: 0
    }

    fun isOverloaded(): Boolean {
        if(usedModifiers > getStat(ToolStat.MODIFIER_SLOTS)) {
            return true
        }
        return traits.any { it.value !in 0..it.key.maxLevel }
    }

    override fun getLore(meta: Any?): List<Component> {
        if(meta !is ToolMeta) {
            return emptyList()
        }

        val statLine = ToolStat.combineStatDisplays(ToolStat.getStatDisplays(stats.values, this, meta))
        val lore = mutableListOf(statLine)

        if(isOverloaded()) {
            lore.add(Component.text("⚖ Overloaded",
                NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD))
        }

        val separator = Component.text("│", NamedTextColor.DARK_GRAY)
        var addSeparatorAtEnd = false

        val traitLore = Trait.getTraitLore(traits)
        if(traitLore.isNotEmpty()) {
            lore.add(separator)
            lore.add(Component.text("♮ ", TextColor.color(0x0086A8))
                .append(Component.text("Traits", NamedTextColor.DARK_AQUA)))
            addSeparatorAtEnd = true
            lore.addAll(traitLore.map { Component.space().append(it) })
        }

        if(addSeparatorAtEnd) lore.add(separator)

        return lore
    }

    override fun getItemTypeName(meta: Any?): String? {
        if(meta !is ToolMeta) {
            return null
        }
        return toolType.name
    }
}
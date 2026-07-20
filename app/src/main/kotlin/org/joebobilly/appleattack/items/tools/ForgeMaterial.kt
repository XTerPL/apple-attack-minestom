package org.joebobilly.appleattack.items.tools

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.color.Color
import org.joebobilly.appleattack.items.ItemTypeNameProvider
import org.joebobilly.appleattack.items.LoreProvider
import org.joebobilly.appleattack.items.tools.traits.Trait
import org.joebobilly.appleattack.items.tools.type.ToolType
import org.joebobilly.appleattack.items.icons.ItemIcon

sealed class ForgeMaterial(val definition: Definition) : LoreProvider, ItemTypeNameProvider {
    class Handle(definition: Definition) : ForgeMaterial(definition) {
        override fun getExtraStats(): List<ToolStat.StatEntry<*>> {
            return emptyList()
        }
        override fun getType(): ForgeMaterialType {
            return ForgeMaterialType.HANDLE
        }
    }
    class Attack(private val attack: Double, definition: Definition) : ForgeMaterial(definition) {
        override fun getExtraStats(): List<ToolStat.StatEntry<*>> {
            return listOf(ToolStat.StatEntry(ToolStat.ATTACK, attack))
        }
        override fun getType(): ForgeMaterialType {
            return ForgeMaterialType.ATTACK
        }
    }

    data class Definition(val modifierSlots: Int, val traitsAdded: Map<Trait, Int>,
                          val partName: String? = null, val color: TextColor? = null, val icon: ItemIcon? = null)

    fun getStats(): List<ToolStat.StatEntry<*>> {
        val list = mutableListOf<ToolStat.StatEntry<*>>(ToolStat.StatEntry(ToolStat.MODIFIER_SLOTS, definition.modifierSlots))
        list.addAll(getExtraStats())
        return list.toList()
    }
    protected abstract fun getExtraStats(): List<ToolStat.StatEntry<*>>
    abstract fun getType(): ForgeMaterialType

    final override fun getLore(meta: Any?): List<Component> {
        val statLine = ToolStat.combineStatDisplays(ToolStat.getStatDisplays(getStats()))
        val lore = mutableListOf(statLine)
        lore.addAll(Trait.getTraitLore(definition.traitsAdded))
        return lore
    }
    final override fun getItemTypeName(meta: Any?): String {
        return getType().itemTypeName
    }

    companion object {
        fun getName(materials: List<ForgeMaterial>, toolType: ToolType): Component {
            if(materials.isEmpty()) {
                return Component.text("Unknown ${toolType.displayName}", NamedTextColor.RED)
            }
            val parts = mutableListOf<String>()
            val colorParts = mutableListOf<TextColor>()
            materials.forEach {
                if(it.definition.partName != null && !parts.contains(it.definition.partName)) {
                    parts.add(it.definition.partName)
                }
                if(it.definition.color != null) colorParts.add(it.definition.color)
            }
            val name = parts.joinToString(separator = "-") + " ${toolType.displayName}"
            val firstColorPart = colorParts.removeFirst()
            val mixedColor = Color(firstColorPart).mixWith(*colorParts.toTypedArray())
            return Component.text(name, TextColor.color(mixedColor))
        }
    }
}
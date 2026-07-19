package org.joebobilly.appleattack.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.joebobilly.appleattack.damage.AttackInfo
import org.joebobilly.appleattack.items.tools.ForgeMaterial
import org.joebobilly.appleattack.items.tools.ForgeUpgradeData
import org.joebobilly.appleattack.items.tools.ToolData

class ItemProperty<T, R> private constructor(
    val name: String,
    val postProcess: (R) -> T,
    val default: ((AAItem<*>?) -> T)? = null,
) {
    companion object {
        val NAME = of("name") {
            Component.text(it?.let { "Unknown Item: ${it.id}" } ?: "Invalid Item", NamedTextColor.DARK_RED)
        }
        val DESCRIPTION = of<List<String>>("description") { emptyList() }
        val RARITY = of("rarity", AARarity.COMMON)
        val ITEM_TYPE_NAME = of<String>("item_type_name")
        val ITEM_MODEL = of("item_model") { it?.backingMaterial?.key()?.asString() ?: "minecraft:barrier" }
        val GLOW = of("glow", false)

        val EXTRA_LORE = of<List<Component>>("extra_lore") { emptyList() }

        val MELEE_ATTACK = of("melee_attack") { AttackInfo.melee(1.0) }

        // forge
        val FORGE_UPGRADE_DATA = of<ForgeUpgradeData>("forge_upgrade_data")
        val FORGE_MATERIAL = of<ForgeMaterial>("forge_material")

        // only makes sense on ToolItem
        val TOOL_DATA = of<ToolData, ToolData.Provider>("tool_data", { it.getToolData() })

        fun <T, R> of(name: String, postProcess: (R) -> T, default: ((AAItem<*>?) -> T)? = null): ItemProperty<T, R> {
            return ItemProperty(name, postProcess, default)
        }
        fun <T> of(name: String, default: ((AAItem<*>?) -> T)? = null): ItemProperty<T, T> {
            return of(name, { it }, default)
        }
        fun <T> of(name: String, default: T): ItemProperty<T, T> {
            return of(name) { default }
        }
    }

    internal class Map<METATYPE> {
        data class PropertyEntry<T, R, METATYPE>(val property: ItemProperty<T, R>, val provider: (METATYPE) -> R)
        private val entries = mutableMapOf<ItemProperty<*, *>, (PropertyEntry<*, *, METATYPE>)>()
        fun <T, R> setEntry(property: ItemProperty<T, R>, provider: (METATYPE) -> R) {
            entries[property] = PropertyEntry(property, provider)
        }
        @Suppress("UNCHECKED_CAST")
        fun <T, R> getEntry(property: ItemProperty<T, R>): PropertyEntry<T, R, METATYPE>? {
            val entry = entries[property] ?: return null
            return entry as PropertyEntry<T, R, METATYPE>
        }
    }

    constructor(name: String, default: T) : this(name, { default })

    fun getDefaultOrThrow(itemType: AAItem<*>?): T {
        if(default == null) throw NoSuchElementException("No property with name $name in item")
        return default(itemType)
    }
}
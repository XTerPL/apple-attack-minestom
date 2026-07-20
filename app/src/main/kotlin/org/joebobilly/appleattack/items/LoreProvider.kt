package org.joebobilly.appleattack.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

interface LoreProvider {
    companion object {
        val PROPERTY_PROVIDES_LORE: List<LoreProviderEntry<*>> = listOf(
            LoreProviderEntry(ItemProperty.DESCRIPTION) {
                value, metaPair -> formatDescription(value)
            },
            LoreProviderEntry(ItemProperty.RARITY) {
                value, metaPair ->
                for(entry in ItemTypeNameProvider.PROPERTY_PROVIDES_ITEM_TYPE_NAME) {
                    val itemTypeName = entry.getItemTypeName(metaPair)
                    if(itemTypeName != null) return@LoreProviderEntry listOf(value.wrapType(itemTypeName))
                }
                listOf(value.displayName)
            },
            wrapProvided(ItemProperty.TOOL_DATA),
            wrapProvided(ItemProperty.FORGE_MATERIAL),
            wrapProvided(ItemProperty.FORGE_UPGRADE_DATA),
            wrapList(ItemProperty.EXTRA_LORE)
        )

        fun <T : LoreProvider> wrapProvided(property: ItemProperty<T, *>): LoreProviderEntry<T> {
            return LoreProviderEntry(property) {
                value, metaPair -> value.getLore(metaPair.meta)
            }
        }
        fun wrapList(property: ItemProperty<List<Component>, *>): LoreProviderEntry<List<Component>> {
            return LoreProviderEntry(property) {
                    value, metaPair -> value
            }
        }

        fun formatDescription(description: List<String>): List<Component> {
            return description.map { line -> Component.empty()
                .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                .append(Component.text(line, NamedTextColor.GRAY))
            }
        }
    }

    data class LoreProviderEntry<T>(val property: ItemProperty<T, *>, val loreProvider: (T, AAItemMetaPair<*>) -> List<Component>) {
        fun <METATYPE> getLore(itemType: AAItem<METATYPE>, meta: METATYPE): List<Component> {
            return itemType.withProperty(property, meta) {
                loreProvider(it, AAItemMetaPair(itemType, meta))
            } ?: emptyList()
        }
    }

    fun getLore(meta: Any?): List<Component>
}
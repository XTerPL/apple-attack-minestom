package org.joebobilly.appleattack.items

interface ItemTypeNameProvider {
    companion object {
        val PROPERTY_PROVIDES_ITEM_TYPE_NAME: List<ItemTypeNameProviderEntry<*>> = listOf(
            wrapString(ItemProperty.ITEM_TYPE_NAME),
            wrapProvided(ItemProperty.TOOL_DATA),
            wrapProvided(ItemProperty.FORGE_MATERIAL),
            wrapValue(ItemProperty.FORGE_UPGRADE_DATA, "upgrade")
        )

        fun <T : ItemTypeNameProvider> wrapProvided(property: ItemProperty<T, *>): ItemTypeNameProviderEntry<T> {
            return ItemTypeNameProviderEntry(property) {
                value, metaPair -> value.getItemTypeName(metaPair.meta)
            }
        }
        fun wrapString(property: ItemProperty<String, *>): ItemTypeNameProviderEntry<String> {
            return ItemTypeNameProviderEntry(property) {
                    value, _ -> value
            }
        }
        fun <T> wrapValue(property: ItemProperty<T, *>, value: String): ItemTypeNameProviderEntry<T> {
            return ItemTypeNameProviderEntry(property) {
                    _, _ -> value
            }
        }
    }

    data class ItemTypeNameProviderEntry<T>(val property: ItemProperty<T, *>, val itemTypeNameProvider: (T, AAItemMetaPair<*>) -> String?) {
        fun <METATYPE> getItemTypeName(metaTypePair: AAItemMetaPair<METATYPE>): String? {
            return metaTypePair.withProperty(property) {
                itemTypeNameProvider(it, metaTypePair)
            }
        }
    }

    fun getItemTypeName(meta: Any?): String?
}
package org.joebobilly.appleattack.items

import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.utils.ValueRegistry

object AAItemManager : ValueRegistry<AAItem<*>>(AAItem<*>::id, "item type") {
    fun getItem(itemStack: ItemStack): AAItem<*>? {
        return itemStack.getTag(AAItem.itemTag)
    }
    fun getItemMetaPair(itemStack: ItemStack): AAItemMetaPair<*>? {
        val itemType = getItem(itemStack) ?: return null
        return itemType.getMetaPair(itemStack)
    }
    fun hasItemProperty(itemStack: ItemStack, property: ItemProperty<*, *>): Boolean {
        val itemMetaPair = getItemMetaPair(itemStack) ?: return property.default != null
        return itemMetaPair.hasProperty(property)
    }
    fun <T> getItemProperty(itemStack: ItemStack, property: ItemProperty<T, *>): T {
        val itemMetaPair = getItemMetaPair(itemStack) ?: return property.getDefaultOrThrow(null)
        return itemMetaPair.getProperty(property)
    }
    fun <T, U> withItemProperty(itemStack: ItemStack, property: ItemProperty<T, *>, consumer: (T) -> U): U? {
        if(hasItemProperty(itemStack, property)) {
            return consumer(getItemProperty(itemStack, property))
        }
        return null
    }
}
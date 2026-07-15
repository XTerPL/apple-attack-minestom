package org.joebobilly.appleattack.items

import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.utils.ValueRegistry

object AAItemManager : ValueRegistry<AAItem<*>>(AAItem<*>::id, "item type") {
    fun getItem(itemStack: ItemStack): AAItem<*>? {
        return itemStack.getTag(AAItem.itemTag)
    }
}
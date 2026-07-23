package org.joebobilly.appleattack.items.predicates

import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.items.AAItem

abstract class TypedItemPredicate<METATYPE>(val itemType: AAItem<METATYPE>) : ItemPredicate {
    override fun test(item: ItemStack): Boolean {
        val meta = itemType.getMeta(item) ?: return false
        return test(meta)
    }

    abstract fun test(meta: METATYPE): Boolean
}
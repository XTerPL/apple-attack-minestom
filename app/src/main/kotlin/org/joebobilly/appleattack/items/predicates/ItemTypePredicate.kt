package org.joebobilly.appleattack.items.predicates

import net.kyori.adventure.text.Component
import org.joebobilly.appleattack.items.AAItem

class ItemTypePredicate<METATYPE : Any>(itemType: AAItem<METATYPE>) : TypedItemPredicate<METATYPE>(itemType) {
    override fun testMeta(meta: METATYPE) = true
    override fun shopName(): Component = itemType.defaultName()
}
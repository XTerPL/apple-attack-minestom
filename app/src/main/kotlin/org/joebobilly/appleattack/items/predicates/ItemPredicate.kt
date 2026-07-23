package org.joebobilly.appleattack.items.predicates

import net.kyori.adventure.text.Component
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.rewards.Cost
import java.util.function.Predicate

interface ItemPredicate : Predicate<ItemStack> {
    companion object {
        fun ItemPredicate.toCost(count: Int = 1): Cost.ItemCost {
            return Cost.ItemCost(this, count)
        }
    }

    fun shopName(): Component
}
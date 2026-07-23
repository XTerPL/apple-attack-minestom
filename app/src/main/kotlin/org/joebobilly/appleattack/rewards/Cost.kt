package org.joebobilly.appleattack.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.items.AAItem
import org.joebobilly.appleattack.items.predicates.ItemPredicate
import org.joebobilly.appleattack.items.predicates.ItemPredicate.Companion.toCost
import org.joebobilly.appleattack.items.predicates.ItemTypePredicate
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.rewards.Currency.Companion.toCost
import org.joebobilly.appleattack.utils.InventoryUtils

class Cost internal constructor(val itemCosts: List<ItemCost>, val currencyCosts: List<CurrencyCost<*>>) {
    class Builder {
        private val itemCosts = mutableListOf<ItemCost>()
        private val currencyCosts = mutableMapOf<Currency<*>, CurrencyCost<*>>()

        fun addCost(cost: ItemCost): Builder {
            itemCosts.add(cost)
            return this
        }

        fun addCost(itemPredicate: ItemPredicate, count: Int = 1) = addCost(itemPredicate.toCost(count))
        fun addCost(itemType: AAItem<*>, count: Int = 1) = addCost(ItemTypePredicate(itemType), count)

        fun <T> addCost(cost: CurrencyCost<T>): Builder {
            if(currencyCosts.containsKey(cost.currency)) {
                currencyCosts[cost.currency] = cost
                return this
            }
            @Suppress("UNCHECKED_CAST")
            val previousAmount = (currencyCosts[cost.currency] as CurrencyCost<T>).amount
            val newAmount = cost.currency.combineAmounts(previousAmount, cost.amount)
            currencyCosts[cost.currency] = CurrencyCost(cost.currency, newAmount)
            return this
        }

        fun <T> addCost(currency: Currency<T>, amount: T) = addCost(currency.toCost(amount))

        fun build(): Cost {
            return Cost(itemCosts.toList(), currencyCosts.values.toList())
        }
    }

    class ItemCost(val itemPredicate: ItemPredicate, val count: Int = 1) {
        internal fun filter(itemStack: ItemStack): Boolean {
            return itemPredicate.test(itemStack)
        }
        internal fun getLoreLine(): Component {
            return Component.empty()
                .append(Component.text("${count}x ", NamedTextColor.GRAY))
                .append(itemPredicate.shopName())
        }
    }
    class CurrencyCost<T>(val currency: Currency<T>, val amount: T) {
        internal fun has(player: AAPlayer): Boolean {
            return currency.hasAmount(player, amount)
        }
        internal fun take(player: AAPlayer) {
            currency.takeAmount(player, amount)
        }
        internal fun getLoreLine(): Component {
            return currency.getComponent(amount)
        }
    }

    fun has(player: AAPlayer): Boolean {
        if(currencyCosts.any { !it.has(player) }) return false
        return getTakenItemMap(player) != null
    }
    fun take(player: AAPlayer): Boolean {
        if(currencyCosts.any { !it.has(player) }) return false
        val takenItemMap = getTakenItemMap(player) ?: return false
        currencyCosts.forEach { it.take(player) }
        InventoryUtils.takeUsingMap(player, takenItemMap)
        return true
    }
    fun getLore(): List<Component> {
        val costLore = mutableListOf<Component>()
        if(currencyCosts.isEmpty() && itemCosts.isEmpty()) {
            costLore.add(Component.text("Free", NamedTextColor.GREEN))
        }
        else {
            costLore.addAll(currencyCosts.map { it.getLoreLine() })
            costLore.addAll(itemCosts.map { it.getLoreLine() })
        }
        val lore = mutableListOf(Component.text("Costs: ", NamedTextColor.GRAY))
        lore.addAll(costLore.map { Component.space().append(it) })
        return lore.toList()
    }
    private fun getTakenItemMap(player: AAPlayer): Map<Int, Int>? {
        val takenItemMap = mutableMapOf<Int, Int>()

        val inventoryCopy = player.inventory.itemStacks.toMutableList().subList(0, 36)
        for(cost in itemCosts) {
            var remaining = cost.count
            for(i in 0..inventoryCopy.size) {
                if(remaining <= 0) break
                val itemStack = inventoryCopy[i]
                if(!cost.filter(itemStack)) continue
                val count = inventoryCopy[i].amount()
                val taken = count.coerceAtMost(remaining)
                remaining -= taken
                takenItemMap[i] = (takenItemMap[i] ?: 0) + taken
                inventoryCopy[i] = itemStack.consume(taken)
            }
            if(remaining > 0) return null
        }

        return takenItemMap.toMap()
    }
}
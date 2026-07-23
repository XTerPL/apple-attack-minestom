package org.joebobilly.appleattack.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.players.AAPlayer

interface Currency<T> {
    companion object {
        fun <T> Currency<T>.toCost(amount: T): Cost.CurrencyCost<T> {
            return Cost.CurrencyCost(this, amount)
        }
    }

    fun hasAmount(player: AAPlayer, amount: T): Boolean
    fun giveAmount(player: AAPlayer, amount: T)
    fun takeAmount(player: AAPlayer, amount: T)
    fun combineAmounts(a: T, b: T): T
    fun getComponent(amount: T): Component
    fun getIcon(amount: T): ItemStack
}
package org.joebobilly.appleattack.interfaces

import net.minestom.server.inventory.click.Click

sealed interface ButtonClick {
    sealed class Modifiable(val modified: Boolean) : ButtonClick
    class Left(modified: Boolean) : Modifiable(modified) {
        override fun name(): String {
            if(modified) return "shift left"
            return "left"
        }
    }
    class Right(modified: Boolean) : Modifiable(modified) {
        override fun name(): String {
            if(modified) return "shift right"
            return "right"
        }
    }
    class Drop(modified: Boolean) : Modifiable(modified) {
        override fun name(): String {
            if(modified) return "control drop"
            return "drop"
        }
    }
    data class Hotbar(val slot: Int) : ButtonClick {
        override fun name(): String {
            return "hotbar $slot"
        }
    }
    object Swap : ButtonClick {
        override fun name(): String {
            return "swap"
        }
    }

    companion object {
        fun fromClick(click: Click): ButtonClick? {
            return when(click) {
                is Click.Left -> Left(false)
                is Click.LeftShift -> Left(true)
                is Click.Right -> Right(false)
                is Click.RightShift -> Right(true)
                is Click.DropSlot -> Drop(click.all)
                is Click.HotbarSwap -> Hotbar(click.hotbarSlot)
                is Click.OffhandSwap -> Swap
                else -> null
            }
        }
    }

    fun name(): String
}
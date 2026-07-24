package org.joebobilly.appleattack.interfaces

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.Player
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.inventory.click.Click
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.events.InventoryEvents
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.rewards.Cost
import org.joebobilly.appleattack.utils.InventoryUtils
import org.joebobilly.appleattack.utils.Sounds

sealed interface Slot {
    sealed class Usable(val cover: ItemStack = ItemStack.AIR, val filter: (ItemStack) -> Boolean = { true }) : Slot {
        var itemStack: ItemStack = ItemStack.AIR
        override fun getIcon(): ItemStack {
            if(itemStack.isAir) return cover
            return itemStack
        }

        fun getItemMetaPair(): AAItemMetaPair<*>? {
            return AAItemManager.getItemMetaPair(itemStack)
        }
    }
    class Storage(cover: ItemStack = ItemStack.AIR, filter: (ItemStack) -> Boolean = { true }) : Usable(cover, filter) {
        fun takeOne() {
            itemStack = itemStack.consume(1)
        }

        override fun handleClick(click: Click, clickInfo: InventoryEvents.ClickInfo) {
            val situation = Situation.getSituation(clickInfo.cursor, itemStack)
            if(click is Click.Left || click is Click.LeftDropCursor) {
                when(situation) {
                    Situation.ALL_AIR -> {}
                    Situation.CURSOR_AIR -> {
                        clickInfo.cursor = itemStack
                        itemStack = ItemStack.AIR
                    }
                    Situation.SLOT_AIR -> {
                        if(filter(clickInfo.cursor.withAmount(1))) {
                            itemStack = clickInfo.cursor
                            clickInfo.cursor = ItemStack.AIR
                        }
                    }
                    Situation.SIMILAR -> {
                        val count = itemStack.amount() + clickInfo.cursor.amount()
                        val maxCount = itemStack.maxStackSize()
                        val slotCount = count.coerceAtMost(maxCount)
                        val cursorCount = count - slotCount

                        clickInfo.cursor = clickInfo.cursor.withAmount(cursorCount)
                        itemStack = itemStack.withAmount(slotCount)
                    }
                    Situation.NOT_SIMILAR -> { swap(clickInfo) }
                }
            }
            if(click is Click.Right || click is Click.RightDropCursor) {
                when(situation) {
                    Situation.ALL_AIR -> {}
                    Situation.CURSOR_AIR -> {
                        val count = itemStack.amount()
                        val remainingCount = count / 2
                        clickInfo.cursor = itemStack.withAmount(count - remainingCount)
                        itemStack = itemStack.withAmount(remainingCount)
                    }
                    Situation.SLOT_AIR -> {
                        if(filter(clickInfo.cursor.withAmount(1))) {
                            itemStack = clickInfo.cursor.withAmount(1)
                            clickInfo.cursor = clickInfo.cursor.consume(1)
                        }
                    }
                    Situation.SIMILAR -> {
                        val maxCount = itemStack.maxStackSize()
                        if(itemStack.amount() < maxCount) {
                            itemStack = itemStack.consume(-1)
                            clickInfo.cursor = clickInfo.cursor.consume(1)
                        }
                    }
                    Situation.NOT_SIMILAR -> { swap(clickInfo) }
                }
            }
            if(click is Click.LeftShift || click is Click.RightShift) {
                itemStack = InventoryUtils.addLikeShiftClick(itemStack, clickInfo.player)
            }
            if(click is Click.DropSlot) {
                if(!itemStack.isAir) {
                    if(click.all) {
                        clickInfo.player.dropItem(itemStack)
                        itemStack = ItemStack.AIR
                    }
                    else {
                        clickInfo.player.dropItem(itemStack.withAmount(1))
                        itemStack = itemStack.consume(1)
                    }
                }
            }
            if(click is Click.HotbarSwap) {
                val hotbarStack = clickInfo.player.inventory.getItemStack(click.hotbarSlot)
                if(hotbarStack.isAir || filter(hotbarStack.withAmount(1))) {
                    clickInfo.player.inventory.setItemStack(click.hotbarSlot, itemStack)
                    itemStack = hotbarStack
                }
            }
        }

        override fun handleShiftClick(clickInfo: InventoryEvents.ClickInfo) {
            val situation = Situation.getSituation(clickInfo.cursor, itemStack)
            when(situation) {
                Situation.ALL_AIR -> {}
                Situation.CURSOR_AIR -> {}
                Situation.SLOT_AIR -> {
                    if(filter(clickInfo.cursor.withAmount(1))) {
                        itemStack = clickInfo.cursor
                        clickInfo.cursor = ItemStack.AIR
                    }
                }
                Situation.SIMILAR -> {
                    val count = itemStack.amount() + clickInfo.cursor.amount()
                    val maxCount = itemStack.maxStackSize()
                    val slotCount = count.coerceAtMost(maxCount)
                    val shiftedCount = count - slotCount
                    itemStack = itemStack.withAmount(slotCount)
                    clickInfo.cursor = clickInfo.cursor.withAmount(shiftedCount)
                }
                Situation.NOT_SIMILAR -> {}
            }
        }

        override fun handlePickup(pickupInfo: InventoryEvents.ClickInfo) {
            if(!itemStack.isSimilar(pickupInfo.cursor)) return
            val count = itemStack.amount() + pickupInfo.cursor.amount()
            val maxCount = itemStack.maxStackSize()
            val cursorCount = count.coerceAtMost(maxCount)
            val slotCount = count - cursorCount
            itemStack = itemStack.withAmount(slotCount)
            pickupInfo.cursor = itemStack.withAmount(cursorCount)
        }

        override fun getMaxStackSizeSituation(): StackSizeSituation {
            return StackSizeSituation.INFINITE
        }

        private fun swap(clickInfo: InventoryEvents.ClickInfo) {
            if(filter(clickInfo.cursor.withAmount(1))) {
                val temp = clickInfo.cursor
                clickInfo.cursor = itemStack
                itemStack = temp
            }
        }
    }
    class Input(cover: ItemStack = ItemStack.AIR, filter: (ItemStack) -> Boolean = { true }) : Usable(cover, filter) {
        override fun handleClick(click: Click, clickInfo: InventoryEvents.ClickInfo) {
            val situation = Situation.getSituation(clickInfo.cursor, itemStack)
            if(click is Click.Left || click is Click.Right || click is Click.LeftDropCursor || click is Click.RightDropCursor) {
                when(situation) {
                    Situation.ALL_AIR -> {}
                    Situation.CURSOR_AIR -> {
                        clickInfo.cursor = itemStack
                        itemStack = ItemStack.AIR
                    }
                    Situation.SLOT_AIR -> {
                        if(filter(clickInfo.cursor.withAmount(1))) {
                            itemStack = clickInfo.cursor.withAmount(1)
                            clickInfo.cursor = clickInfo.cursor.consume(1)
                        }
                    }
                    Situation.SIMILAR -> {
                        val maxCount = itemStack.maxStackSize()
                        if(clickInfo.cursor.amount() < maxCount) {
                            clickInfo.cursor = clickInfo.cursor.consume(-1)
                            itemStack = ItemStack.AIR
                        }
                    }
                    Situation.NOT_SIMILAR -> {
                        if(clickInfo.cursor.amount() == 1 && filter(clickInfo.cursor.withAmount(1))) {
                            val temp = clickInfo.cursor
                            clickInfo.cursor = itemStack
                            itemStack = temp
                        }
                    }
                }
            }
            if(click is Click.LeftShift || click is Click.RightShift) {
                itemStack = InventoryUtils.addLikeShiftClick(itemStack, clickInfo.player)
            }
            if(click is Click.DropSlot) {
                if(!itemStack.isAir) {
                    clickInfo.player.dropItem(itemStack)
                    itemStack = ItemStack.AIR
                }
            }
            if(click is Click.HotbarSwap) {
                val hotbarStack = clickInfo.player.inventory.getItemStack(click.hotbarSlot)
                if(hotbarStack.isAir || (hotbarStack.amount() == 1 && filter(hotbarStack.withAmount(1)))) {
                    clickInfo.player.inventory.setItemStack(click.hotbarSlot, itemStack)
                    itemStack = hotbarStack
                }
            }
        }

        override fun handleShiftClick(clickInfo: InventoryEvents.ClickInfo) {
            val situation = Situation.getSituation(clickInfo.cursor, itemStack)
            if(situation != Situation.SLOT_AIR) return
            if(filter(clickInfo.cursor.withAmount(1))) {
                itemStack = clickInfo.cursor.withAmount(1)
                clickInfo.cursor = clickInfo.cursor.consume(1)
            }
        }

        override fun handlePickup(pickupInfo: InventoryEvents.ClickInfo) {
            if(!itemStack.isSimilar(pickupInfo.cursor)) return
            val maxCount = itemStack.maxStackSize()
            if(pickupInfo.cursor.amount() >= maxCount) return
            pickupInfo.cursor = pickupInfo.cursor.consume(-1)
            itemStack = ItemStack.AIR
        }

        override fun getMaxStackSizeSituation(): StackSizeSituation {
            return StackSizeSituation.SINGLE
        }
    }
    abstract class Output : Slot {
        final override fun getIcon(): ItemStack {
            val icon = getResult()
            if(!icon.isAir) return getCost().addCostLore(icon, false)
            return getNoResult()
        }

        final override fun handleClick(click: Click, clickInfo: InventoryEvents.ClickInfo) {
            if(clickInfo.player !is AAPlayer) return
            val result = getResult()
            val cost = getCost()
            if(result.isAir) {
                onFailure(clickInfo.player)
                return
            }
            if(click is Click.Left || click is Click.LeftDropCursor
                || click is Click.Right || click is Click.RightDropCursor) {
                if(clickInfo.cursor.isSimilar(result) && cost.take(clickInfo.player)) {
                    val newCount = clickInfo.cursor.amount() + result.amount()
                    if(newCount < clickInfo.cursor.maxStackSize()) {
                        clickInfo.cursor = clickInfo.cursor.consume(-newCount)
                        onSuccess(clickInfo.player)
                        onOverallSuccess(clickInfo.player)
                    }
                    else {
                        onFailure(clickInfo.player)
                    }
                }
                else if(clickInfo.cursor.isAir && cost.take(clickInfo.player)) {
                    clickInfo.cursor = result
                    onSuccess(clickInfo.player)
                    onOverallSuccess(clickInfo.player)
                }
                else {
                    onFailure(clickInfo.player)
                }
                return
            }
            if(click is Click.HotbarSwap) {
                if(clickInfo.player.inventory.getItemStack(click.hotbarSlot).isAir && cost.take(clickInfo.player)) {
                    clickInfo.player.inventory.setItemStack(click.hotbarSlot, result)
                    onSuccess(clickInfo.player)
                    onOverallSuccess(clickInfo.player)
                }
                else {
                    onFailure(clickInfo.player)
                }
                return
            }
            if(click is Click.LeftShift || click is Click.RightShift) {
                var times = 0
                while(true) {
                    val currentCost = getCost()
                    val currentResult = getResult()
                    if(!currentResult.isSimilar(result)) break
                    if(!clickInfo.player.inventory.addItemStack(currentResult, TransactionOption.DRY_RUN) ||
                        !currentCost.take(clickInfo.player)) break
                    InventoryUtils.addLikeShiftClick(currentResult, clickInfo.player)
                    onSuccess(clickInfo.player)
                    times++
                }
                if(times == 0) {
                    onFailure(clickInfo.player)
                }
                else {
                    onOverallSuccess(clickInfo.player)
                }
                return
            }
            if(click is Click.DropSlot) {
                if(click.all) {
                    var times = 0
                    while(true) {
                        val currentCost = getCost()
                        val currentResult = getResult()
                        if(!currentResult.isSimilar(result) ||
                            !currentCost.take(clickInfo.player)) break
                        clickInfo.player.dropItem(currentResult)
                        onSuccess(clickInfo.player)
                        times++
                    }
                    if(times == 0) {
                        onFailure(clickInfo.player)
                    }
                    else {
                        onOverallSuccess(clickInfo.player)
                    }
                }
                else if(cost.take(clickInfo.player)) {
                    clickInfo.player.dropItem(result)
                    onSuccess(clickInfo.player)
                    onOverallSuccess(clickInfo.player)
                }
                else {
                    onFailure(clickInfo.player)
                }
                return
            }
            if(click is Click.OffhandSwap) {
                if(canSwapHands(clickInfo.player) && cost.take(clickInfo.player)) {
                    onSuccess(clickInfo.player)
                    onOverallSuccess(clickInfo.player)
                    onSuccessfulSwapHands(result, clickInfo.player)
                    return
                }
            }
            onFailure(clickInfo.player)
        }

        abstract fun getResult(): ItemStack
        open fun getNoResult(): ItemStack {
            return ItemStack.builder(Material.BARRIER)
                .set(DataComponents.ITEM_NAME, Component.text("No Result", NamedTextColor.RED))
                .build()
        }
        abstract fun onSuccess(player: Player)
        abstract fun onOverallSuccess(player: Player)
        open fun onFailure(player: Player) {
            player.playSound(Sounds.BLOCKED_OUTPUT)
        }

        open fun canSwapHands(player: Player): Boolean {
            return false
        }
        open fun onSuccessfulSwapHands(result: ItemStack, player: Player) {}
        open fun getCost(): Cost {
            return Cost.free()
        }
    }
    abstract class Button : Slot {
        abstract fun onClick(player: Player, click: ButtonClick)

        final override fun handleClick(click: Click, clickInfo: InventoryEvents.ClickInfo) {
            val buttonClick = ButtonClick.fromClick(click) ?: return
            onClick(clickInfo.player, buttonClick)
        }
    }

    enum class Situation {
        ALL_AIR,
        CURSOR_AIR,
        SLOT_AIR,
        SIMILAR,
        NOT_SIMILAR;

        companion object {
            fun getSituation(cursor: ItemStack, slot: ItemStack): Situation {
                if(cursor.isAir && slot.isAir) return ALL_AIR
                if(cursor.isAir) return CURSOR_AIR
                if(slot.isAir) return SLOT_AIR
                if(slot.isSimilar(cursor)) return SIMILAR
                return NOT_SIMILAR
            }
        }
    }
    enum class StackSizeSituation {
        NONE, SINGLE, INFINITE
    }

    fun getIcon(): ItemStack

    fun handleClick(click: Click, clickInfo: InventoryEvents.ClickInfo)
    fun handleShiftClick(clickInfo: InventoryEvents.ClickInfo) {}
    fun handlePickup(pickupInfo: InventoryEvents.ClickInfo) {}
    fun getMaxStackSizeSituation(): StackSizeSituation {
        return StackSizeSituation.NONE
    }
}
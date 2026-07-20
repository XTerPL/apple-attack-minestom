package org.joebobilly.appleattack.events

import net.minestom.server.entity.Player
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.inventory.AbstractInventory
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.inventory.click.Click
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.interfaces.Slot
import org.joebobilly.appleattack.interfaces.Slot.Situation
import org.joebobilly.appleattack.interfaces.UserInterfaceInventory

object InventoryEvents {
    class ClickInfo(var cursor: ItemStack, val player: Player)

    const val THE_FORBIDDEN_OFFHAND_SLOT = 45

    fun init(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(InventoryPreClickEvent::class.java) {
            if(it.click is Click.Double) {
                if(it.inventory is PlayerInventory && it.slot == THE_FORBIDDEN_OFFHAND_SLOT) {
                    it.isCancelled = true
                    return@addListener
                }

                val openInventory = it.player.openInventory
                if(openInventory is UserInterfaceInventory) {
                    handlePickup(it.player, openInventory)
                }
                return@addListener
            }

            val inventory = it.inventory
            if(inventory is PlayerInventory) {
                if(it.click.slot() == THE_FORBIDDEN_OFFHAND_SLOT) {
                    it.isCancelled = true
                    return@addListener
                }

                if(it.click is Click.LeftShift || it.click is Click.RightShift) {
                    val openInventory = it.player.openInventory
                    if(openInventory is UserInterfaceInventory) {
                        it.isCancelled = true
                        val clickInfo = ClickInfo(it.clickedItem, it.player)
                        for(i in 0..<openInventory.size) {
                            openInventory.slots[i]?.handleShiftClick(clickInfo)
                            if(clickInfo.cursor.isAir) break
                        }
                        openInventory.updateContents()
                        inventory.setItemStack(it.slot, clickInfo.cursor)
                    }
                }

                if(it.click is Click.OffhandSwap) {
                    it.isCancelled = true
                }

                val click = it.click
                if(click is Click.Drag) {
                    it.click = sanitizeDragClick(click, null)
                }

                return@addListener
            }
            if(inventory !is UserInterfaceInventory) {
                val click = it.click
                if(click is Click.Drag) {
                    it.click = sanitizeDragClick(click, inventory)
                }

                return@addListener
            }

            it.isCancelled = true
            val click = it.click

            if(click is Click.Drag) {
                handleDrag(it, inventory, click)
                return@addListener
            }

            if(click is Click.DropCursor) {
                return@addListener
            }

            val clickInfo = ClickInfo(it.player.inventory.cursorItem, it.player)

            inventory.slots[click.slot()]?.handleClick(click, clickInfo)
            inventory.updateContents()

            it.player.inventory.cursorItem = clickInfo.cursor
        }
        eventHandler.addListener(InventoryCloseEvent::class.java) {
            val inventory = it.inventory
            if(inventory is UserInterfaceInventory) {
                inventory.userInterface.onClose(it.player)
            }
        }
        eventHandler.addListener(PlayerSwapItemEvent::class.java) {
            it.isCancelled = true
        }
    }

    private fun handleDrag(event: InventoryPreClickEvent, inventory: UserInterfaceInventory, click: Click.Drag) {
        val slotList = click.slots().filter {
            (it >= inventory.size && it - inventory.size != THE_FORBIDDEN_OFFHAND_SLOT) || inventory.slots[it] is Slot.Usable
        }
        val amountPer: Int = if(click is Click.LeftDrag) {
            event.player.inventory.cursorItem.amount() / slotList.size
        } else {
            1
        }

        var distributed = 0

        for(slot in slotList) {
            if(slot < inventory.size) {
                val clickInfo = ClickInfo(event.player.inventory.cursorItem.withAmount(amountPer), event.player)
                inventory.slots[slot]?.handleShiftClick(clickInfo)
                distributed += amountPer - clickInfo.cursor.amount()
            }
            else {
                val modifiedSlot = slot - inventory.size
                val itemStack = event.player.inventory.getItemStack(modifiedSlot)
                val shifted = event.player.inventory.cursorItem.withAmount(amountPer)
                val situation = Situation.getSituation(
                    shifted,
                    itemStack
                )
                when(situation) {
                    Situation.ALL_AIR -> {}
                    Situation.CURSOR_AIR -> {}
                    Situation.SLOT_AIR -> {
                        event.player.inventory.setItemStack(modifiedSlot, shifted)
                        distributed += shifted.amount()
                    }
                    Situation.SIMILAR -> {
                        val count = itemStack.amount() + shifted.amount()
                        val maxCount = itemStack.maxStackSize()
                        val slotCount = count.coerceAtMost(maxCount)
                        event.player.inventory.setItemStack(modifiedSlot, itemStack.withAmount(slotCount))
                        distributed += slotCount
                    }
                    Situation.NOT_SIMILAR -> {}
                }
            }
        }

        event.player.inventory.cursorItem = event.player.inventory.cursorItem.consume(distributed)

        inventory.updateContents()
    }

    private fun handlePickup(player: Player, openInventory: UserInterfaceInventory) {
        val pickupInfo = ClickInfo(player.inventory.cursorItem, player)
        for(i in 0..<openInventory.size) {
            if(pickupInfo.cursor.amount() >= pickupInfo.cursor.maxStackSize()) break
            openInventory.slots[i]?.handlePickup(pickupInfo)
        }
        for(i in 0..<player.inventory.size) {
            if(pickupInfo.cursor.amount() >= pickupInfo.cursor.maxStackSize()) break
            val itemStack = player.inventory.getItemStack(i)
            if(!itemStack.isSimilar(pickupInfo.cursor)) continue
            val count = itemStack.amount() + pickupInfo.cursor.amount()
            val maxCount = itemStack.maxStackSize()
            val cursorCount = count.coerceAtMost(maxCount)
            val slotCount = count - cursorCount
            player.inventory.setItemStack(i, itemStack.withAmount(slotCount))
            pickupInfo.cursor = itemStack.withAmount(cursorCount)
        }
        player.inventory.cursorItem = pickupInfo.cursor
        openInventory.updateContents()
    }

    private fun sanitizeDragClick(click: Click.Drag, inventory: AbstractInventory?): Click.Drag {
        val slotList = if(inventory == null) {
            click.slots().filter {
                it != THE_FORBIDDEN_OFFHAND_SLOT
            }
        } else {
            click.slots().filter {
                it < inventory.size || it - inventory.size != THE_FORBIDDEN_OFFHAND_SLOT
            }
        }

        return when(click) {
            is Click.LeftDrag -> Click.LeftDrag(slotList)
            is Click.MiddleDrag -> Click.MiddleDrag(slotList)
            is Click.RightDrag -> Click.RightDrag(slotList)
        }
    }
}
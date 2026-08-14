package org.joebobilly.appleattack.listeners

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.PlayerInventory
import org.joebobilly.appleattack.holograms.HologramManager
import org.joebobilly.appleattack.serialization.PaperSerializationEntry.Companion.persistentDataEntry
import org.joebobilly.appleattack.utils.PositionList
import org.joebobilly.appleattack.utils.Sounds

object PositionListListener : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val mainHand = event.player.inventory.itemInMainHand
        val positionList = PositionList.POSITION_LIST_ENTRY.persistentDataEntry.get(mainHand.persistentDataContainer) ?: return

        if(event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR) {
            if(positionList.addPosition(event.player.location)) {
                event.player.playSound(Sounds.POSITION_ADD)
            }
            else {
                event.player.sendMessage(Component.text(
                    "Can't add your current position to this position list " +
                    "as the list was created in a different world!", NamedTextColor.RED
                ))
                event.player.playSound(Sounds.BLOCKED)
                return
            }
        }
        else return

        mainHand.editPersistentDataContainer {
            PositionList.POSITION_LIST_ENTRY.persistentDataEntry.set(it, positionList)
        }
        PositionList.updatePositionListItem(mainHand)
    }

    @EventHandler
    fun onChangeInventory(event: PlayerInventorySlotChangeEvent) {
        val player = event.player
        val inventory = player.openInventory.getInventory(event.rawSlot)
        if(inventory !is PlayerInventory) return
        if(event.slot != inventory.heldItemSlot) return
        val mainHand = inventory.itemInMainHand
        val positionList = PositionList.POSITION_LIST_ENTRY.persistentDataEntry.get(mainHand.persistentDataContainer)
            ?: return removePositionListHologram(player)
        positionList.showToPlayer(player)
    }

    @EventHandler
    fun onSlotChanged(event: PlayerItemHeldEvent) {
        val player = event.player
        val mainHand = player.inventory.getItem(event.newSlot)
            ?: return removePositionListHologram(player)
        val positionList = PositionList.POSITION_LIST_ENTRY.persistentDataEntry.get(mainHand.persistentDataContainer)
            ?: return removePositionListHologram(player)
        positionList.showToPlayer(player)
    }

    fun removePositionListHologram(player: Player) {
        HologramManager.getHologramPlayer(player).removeDisplay(PositionList.POSITION_LIST_KEY)
    }
}
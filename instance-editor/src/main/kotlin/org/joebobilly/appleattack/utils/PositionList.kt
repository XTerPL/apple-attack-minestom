package org.joebobilly.appleattack.utils

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.thenextlvl.hologram.action.ActionTypes
import net.thenextlvl.hologram.action.ClickAction
import net.thenextlvl.hologram.action.ClickType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.joebobilly.appleattack.holograms.HologramManager
import org.joebobilly.appleattack.utils.PositionUtils.fromPaperLocation
import org.joebobilly.appleattack.utils.PositionUtils.toPaperLocation
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTCopySerializer
import org.joebobilly.appleattack.serialization.PaperSerializationEntry.Companion.persistentDataEntry
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.list
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes
import org.joml.Vector3f

class PositionList {
    companion object {
        val POSITION_LIST_KEY = KeyUtils.of("position_list")
        val POSITION_LIST_ENTRY = Serializer.toEntry(POSITION_LIST_KEY)

        fun createPositionListItem(): ItemStack {
            val itemStack = ItemStack.of(Material.GLOBE_BANNER_PATTERN)
            itemStack.editPersistentDataContainer {
                POSITION_LIST_ENTRY.persistentDataEntry.set(it, PositionList())
            }
            updatePositionListItem(itemStack)
            return itemStack
        }

        @Suppress("UnstableApiUsage")
        fun updatePositionListItem(itemStack: ItemStack) {
            val positionList = POSITION_LIST_ENTRY.persistentDataEntry.get(itemStack.persistentDataContainer)
            if(positionList != null) {
                itemStack.setData(
                    DataComponentTypes.ITEM_NAME, Component.text("Position List")
                )
                if(positionList.size == 0) {
                    itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(listOf(
                        Component.text("Empty", NamedTextColor.DARK_GRAY)
                    )))
                }
                else {
                    val world = positionList.getWorld()
                    val name = world?.name ?: "Unknown"

                    itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(listOf(
                        Component.text("World: $name", NamedTextColor.DARK_GRAY),
                        Component.text("${positionList.size} positions", NamedTextColor.DARK_GRAY)
                    )))
                }
            }
        }
    }

    private var world: Key? = null
    private val positions = mutableListOf<Position>()
    val size get() = positions.size

    fun addPosition(location: Location): Boolean {
        if(location.world == null) return false
        if(world == null) {
            world = location.world.key
        }
        if(location.world.key == world) {
            positions.add(Position.fromPaperLocation(location))
            return true
        }
        return false
    }

    fun removePositionAt(index: Int): Boolean {
        if(index in 0..<positions.size) {
            positions.removeAt(index)
            cleanupWorld()
            return true
        }
        return false
    }

    fun getWorld(): World? {
        return Bukkit.getWorld(world ?: return null)
    }

    fun showToPlayer(player: Player) {
        val display = HologramManager.getHologramPlayer(player).createDisplay(POSITION_LIST_KEY)
        val world = getWorld() ?: return
        for(i in 0..<positions.size) {
            val pos = positions[i]
            val location = pos.toPaperLocation(world).setRotation(0f, 0f)
            display.addHologram(location) {
                val line = it.addItemLine()
                line.isGlowing = true
                line.itemStack = ItemStack.of(Material.TARGET)
                line.billboard = Display.Billboard.FIXED
                line.transformation = TransformationUtils.scale(Vector3f(0.5f))
                @Suppress("UnstableApiUsage") // why is ActionTypes internal???
                val removeAction = ClickAction.factory().create(
                    ActionTypes.types().runCommand(),
                    EnumUtils.setOf(ClickType.LEFT),
                    "positionlist remove $i"
                )
                line.addAction("remove", removeAction)
            }
        }
    }

    private fun cleanupWorld() {
        if(positions.isEmpty()) {
            world = null
        }
    }

    object Serializer : NBTCopySerializer<PositionList> {
        private val world = SerializationTypes.KEY.toEntry("world")
        private val positions = Position.Serializer.list().toEntry("positions")

        override val klass = PositionList::class

        override fun read(context: DeserializationContext): PositionList {
            val result = PositionList()
            if(context.isEmpty()) return result
            result.world = context.read(world)
            result.positions.addAll(context.read(positions))
            return result
        }

        override fun write(context: SerializationContext, value: PositionList) {
            context.write(world, value.world ?: return)
            context.write(positions, value.positions)
        }

        override fun copy(value: PositionList): PositionList {
            val result = PositionList()
            if(value.world == null) return result
            result.world = value.world
            result.positions.addAll(value.positions)
            return result
        }
    }
}
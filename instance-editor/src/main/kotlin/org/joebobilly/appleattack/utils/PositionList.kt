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
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.joebobilly.appleattack.InstanceEditor
import org.joebobilly.appleattack.holograms.HologramManager
import org.joml.Vector3f

class PositionList {
    companion object {
        val POSITION_LIST_KEY = InstanceEditor.key("position_list")
        val POSITION_LIST_ENTRY = PersistentDataEntry(POSITION_LIST_KEY, PersistentType)

        fun createPositionListItem(): ItemStack {
            val itemStack = ItemStack.of(Material.GLOBE_BANNER_PATTERN)
            itemStack.editPersistentDataContainer {
                POSITION_LIST_ENTRY.set(it, PositionList())
            }
            updatePositionListItem(itemStack)
            return itemStack
        }

        @Suppress("UnstableApiUsage")
        fun updatePositionListItem(itemStack: ItemStack) {
            val positionList = POSITION_LIST_ENTRY.get(itemStack.persistentDataContainer)
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
            positions.add(Position(location))
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
            val location = pos.toLocation(world).setRotation(0f, 0f)
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

    data class Position(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float) {
        constructor(location: Location) : this(location.x, location.y, location.z, location.yaw, location.pitch)

        fun toLocation(world: World): Location {
            return Location(world, this.x, this.y, this.z, this.yaw, this.pitch)
        }

        object PersistentType : PersistentDataType<PersistentDataContainer, Position> {
            private val xEntry = PersistentDataEntry("x", PersistentDataType.DOUBLE)
            private val yEntry = PersistentDataEntry("y", PersistentDataType.DOUBLE)
            private val zEntry = PersistentDataEntry("z", PersistentDataType.DOUBLE)
            private val yawEntry = PersistentDataEntry("yaw", PersistentDataType.FLOAT)
            private val pitchEntry = PersistentDataEntry("pitch", PersistentDataType.FLOAT)

            override fun getPrimitiveType(): Class<PersistentDataContainer> = PersistentDataContainer::class.java
            override fun getComplexType() = Position::class.java

            override fun toPrimitive(value: Position, ctx: PersistentDataAdapterContext): PersistentDataContainer {
                val container = ctx.newPersistentDataContainer()
                xEntry.set(container, value.x)
                yEntry.set(container, value.y)
                zEntry.set(container, value.z)
                pitchEntry.set(container, value.yaw)
                yawEntry.set(container, value.pitch)
                return container
            }

            override fun fromPrimitive(value: PersistentDataContainer, ctx: PersistentDataAdapterContext): Position {
                val x = xEntry.getOrDefault(value, 0.0)
                val y = yEntry.getOrDefault(value, 0.0)
                val z = zEntry.getOrDefault(value, 0.0)
                val yaw = yawEntry.getOrDefault(value, 0f)
                val pitch = pitchEntry.getOrDefault(value, 0f)
                return Position(x, y, z, yaw, pitch)
            }
        }
    }

    object PersistentType : PersistentDataType<PersistentDataContainer, PositionList> {
        private val worldEntry = PersistentDataEntry("world", KeyPersistentType)
        private val positionsEntry = PersistentDataEntry("positions",
            PersistentDataType.LIST.listTypeFrom(Position.PersistentType))

        override fun getPrimitiveType() = PersistentDataContainer::class.java
        override fun getComplexType() = PositionList::class.java

        override fun toPrimitive(value: PositionList, ctx: PersistentDataAdapterContext): PersistentDataContainer {
            val container = ctx.newPersistentDataContainer()
            if(value.world == null) {
                return container
            }
            worldEntry.set(container, value.world!!)
            positionsEntry.set(container, value.positions)
            return container
        }

        override fun fromPrimitive(value: PersistentDataContainer, ctx: PersistentDataAdapterContext): PositionList {
            val result = PositionList()
            if(value.isEmpty) return result
            result.world = worldEntry.get(value) ?: throw IllegalArgumentException("world not found")
            result.positions.addAll(positionsEntry.get(value) ?: throw IllegalArgumentException("positions not found"))
            return result
        }
    }
}
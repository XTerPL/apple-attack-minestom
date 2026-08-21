package org.joebobilly.appleattack.entities.spawners

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.craftbukkit.entity.CraftEntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.joebobilly.appleattack.InstanceEditor
import org.joebobilly.appleattack.entities.EntityTypeClass
import org.joebobilly.appleattack.holograms.HologramActionTypes
import org.joebobilly.appleattack.holograms.HologramActionTypes.left
import org.joebobilly.appleattack.holograms.HologramActionTypes.right
import org.joebobilly.appleattack.holograms.HologramCallbackEvent
import org.joebobilly.appleattack.holograms.HologramDisplay
import org.joebobilly.appleattack.holograms.HologramDisplayMap
import org.joebobilly.appleattack.holograms.HologramManager
import org.joebobilly.appleattack.infodump.EntityDumpEntry
import org.joebobilly.appleattack.serialization.PaperSerializationEntry.Companion.persistentDataEntry
import org.joebobilly.appleattack.serialization.SerializationType.Companion.list
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.utils.KeyUtils
import org.joebobilly.appleattack.utils.PositionUtils.toPaperLocation
import org.joebobilly.appleattack.utils.SchedulerUtils.runTaskLater
import org.joebobilly.appleattack.utils.Sounds
import org.joebobilly.appleattack.utils.TransformationUtils
import org.joebobilly.appleattack.utils.actionButton
import org.joebobilly.appleattack.utils.dialog
import org.joebobilly.appleattack.utils.dynamicCallback
import org.joml.Vector3f

object SpawnerManager : HologramDisplayMap<Pair<Key, Int>>(), Listener {
    private val highlightKey = KeyUtils.of("spawner_highlight")
    private val editKey = KeyUtils.of("edit_spawner")
    private val entitySpawners = EntitySpawnerData.Serializer.list().toEntry("entity_spawners")

    override val namespace = "spawner"
    override fun stringifyIdentifier(identifier: Pair<Key, Int>)
        = identifier.first.namespace() + "--" + identifier.first.value() + "--" + identifier.second

    fun addToWorld(world: World, entitySpawnerData: EntitySpawnerData) {
        val persistentDataContainer = world.persistentDataContainer
        val spawners = entitySpawners.persistentDataEntry.getOrDefault(
            world.persistentDataContainer, emptyList()
        ).toMutableList()
        spawners.add(entitySpawnerData)
        entitySpawners.persistentDataEntry.set(persistentDataContainer, spawners)
        update()
    }

    fun removeFromWorld(world: World, index: Int) {
        val persistentDataContainer = world.persistentDataContainer
        val spawners = entitySpawners.persistentDataEntry.getOrDefault(
            world.persistentDataContainer, emptyList()
        ).toMutableList()
        if(index !in spawners.indices) return
        spawners.removeAt(index)
        entitySpawners.persistentDataEntry.set(persistentDataContainer, spawners)
        update()
    }

    fun update() {
        cleanup()
        for(world in Bukkit.getWorlds()) {
            val spawners = entitySpawners.persistentDataEntry.getOrDefault(world.persistentDataContainer, emptyList())
            for((index, spawner) in spawners.withIndex()) {
                createDisplay(world.key to index).apply { prepareDisplay(this, spawner, world, index) }
            }
        }
    }

    private fun prepareDisplay(display: HologramDisplay, spawner: EntitySpawnerData, world: World, index: Int) {
        val entityType = InstanceEditor.infoDump.getEntityType(spawner.entityTypeId)
        val scale = getEntityTypeScale(entityType)
        val title = getEntityTitle(entityType, spawner.entityTypeId)
        val material = getMaterial(entityType)

        for(pos in spawner.positions.map { it.zeroRotation.toPaperLocation(world) }) {
            display.addHologram(pos) {
                val line = it.addItemLine()
                line.itemStack = ItemStack.of(material)
                line.billboard = org.bukkit.entity.Display.Billboard.FIXED
                line.transformation = TransformationUtils.scale(scale)
                line.addAction("highlight-spawner",
                    HologramActionTypes.callback().left(
                        HologramCallbackEvent.createCallback(highlightKey, index.toString())
                    )
                )
                line.addAction("edit-spawner",
                    HologramActionTypes.callback().right(
                        HologramCallbackEvent.createCallback(editKey, index.toString())
                    )
                )
            }
            display.addHologram(pos.clone().add(0.0, scale.y.toDouble() + 0.5, 0.0)) {
                val line = it.addTextLine()
                line.setText(title)
            }
        }
    }

    private fun getEntityTypeScale(entityType: EntityDumpEntry?): Vector3f {
        val entityDisplayType = entityType?.let { RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE)
            .get(Key.key(it.startingEntityTypeId)) }
        val craftEntityDisplayType = entityDisplayType?.let { CraftEntityType.bukkitToMinecraft(it) }
        return craftEntityDisplayType?.dimensions?.let {
            Vector3f(it.width, it.height, it.width)
        } ?: Vector3f(0.5f)
    }

    private fun getEntityTitle(entityType: EntityDumpEntry?, elseId: String): Component {
        return entityType?.entityName ?: Component.text("Unknown entity type: $elseId")
    }

    private fun getMaterial(entityType: EntityDumpEntry?): Material {
        return when(entityType?.entityClass) {
            EntityTypeClass.MOB -> Material.RED_STAINED_GLASS
            EntityTypeClass.NPC -> Material.LIME_STAINED_GLASS
            null -> Material.GRAY_STAINED_GLASS
        }
    }

    fun getSpawner(world: World, index: Int): EntitySpawnerData? {
        val spawners = entitySpawners.persistentDataEntry.getOrDefault(world.persistentDataContainer, emptyList())
        return spawners.getOrNull(index)
    }

    fun highlightSpawner(player: Player, world: World, index: Int) {
        player.playSound(Sounds.HIGHLIGHT_SPAWNER)
        val display = HologramManager.getHologramPlayer(player).createDisplay(highlightKey)
        val spawner = getSpawner(world, index) ?: return

        val entityType = InstanceEditor.infoDump.getEntityType(spawner.entityTypeId)
        val scale = getEntityTypeScale(entityType)

        for(pos in spawner.positions.map { it.zeroRotation.toPaperLocation(world) }) {
            display.addHologram(pos) {
                val line = it.addItemLine()
                line.isGlowing = true
                line.itemStack = ItemStack.of(Material.GLASS)
                line.billboard = org.bukkit.entity.Display.Billboard.FIXED
                line.transformation = TransformationUtils.scale(scale)
            }
        }

        Bukkit.getScheduler().runTaskLater(5 * 20L) {
            if(!display.isRemoved) {
                display.remove()
            }
        }
    }

    fun editSpawner(player: Player, world: World, index: Int) {
        val spawner = getSpawner(world, index) ?: return
        val entityType = InstanceEditor.infoDump.getEntityType(spawner.entityTypeId)

        val title = getEntityTitle(entityType, spawner.entityTypeId)

        player.showDialog(
            dialog(title) {
                body {
                    message(Component.text("hi"))
                }
                inputs {
                    @Suppress("UnstableApiUsage")
                    numberRange("max_spawned", Component.text("Max Spawned"), 1f, 100f) {
                        step(1f)
                        initial(spawner.maxSpawned.toFloat())
                    }
                }
                multiAction {
                    add(Component.text("Remove")) {
                        ClickEvent.showDialog(
                            dialog(title) {
                                body {
                                    message(Component.text("Are you sure you want to remove this spawner?"))
                                }
                                confirmation(
                                    actionButton(Component.text("Yes")) {
                                        ClickEvent.callback {
                                            removeFromWorld(world, index)
                                        }.dialog
                                    },
                                    actionButton(Component.text("No")) {
                                        null
                                    }
                                )
                            }
                        ).dialog
                    }
                    add(Component.text("Update")) {
                        @Suppress("UnstableApiUsage")
                        dynamicCallback {
                            dialog, audience ->
                            removeFromWorld(world, index)
                            addToWorld(world, EntitySpawnerData(
                                spawner.entityTypeId, spawner.positions,
                                (dialog.getFloat("max_spawned")?.toInt() ?: 1).coerceAtLeast(1)
                            ))
                            audience.closeDialog()
                        }
                    }
                }
            }
        )
    }

    @EventHandler
    fun onHologramCallback(event: HologramCallbackEvent) {
        event.checkCallbackCommand(highlightKey, 1)?.let {
            val index = it[0].toIntOrNull() ?: return
            highlightSpawner(event.player, event.line.world, index)
            return
        }
        event.checkCallbackCommand(editKey, 1)?.let {
            val index = it[0].toIntOrNull() ?: return
            editSpawner(event.player, event.line.world, index)
            return
        }
    }
}
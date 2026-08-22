package org.joebobilly.appleattack.entities.spawners

import io.papermc.paper.dialog.Dialog
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
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes
import org.joebobilly.appleattack.utils.KeyUtils
import org.joebobilly.appleattack.utils.PositionList
import org.joebobilly.appleattack.utils.PositionUtils.toPaperLocation
import org.joebobilly.appleattack.utils.SchedulerUtils.runTaskLater
import org.joebobilly.appleattack.utils.Sounds
import org.joebobilly.appleattack.utils.TransformationUtils
import org.joebobilly.appleattack.utils.actionButton
import org.joebobilly.appleattack.utils.dialog
import org.joebobilly.appleattack.utils.dynamicCallback
import org.joml.Vector3f
import kotlin.uuid.Uuid

object SpawnerManager : HologramDisplayMap<Pair<Key, Uuid>>(), Listener {
    private val highlightKey = KeyUtils.of("spawner_highlight")
    private val editKey = KeyUtils.of("edit_spawner")
    private val entitySpawners = SerializationTypes.mapUUID(EntitySpawnerData.Serializer).toEntry("entity_spawners")

    override val namespace = "spawner"
    override fun stringifyIdentifier(identifier: Pair<Key, Uuid>)
        = identifier.first.namespace() + "--" + identifier.first.value() + "--" + identifier.second.toHexDashString()

    fun addToWorld(world: World, entitySpawnerData: EntitySpawnerData) {
        val persistentDataContainer = world.persistentDataContainer
        val spawners = entitySpawners.persistentDataEntry.getOrDefault(
            persistentDataContainer, emptyMap()
        ).toMutableMap()
        spawners[Uuid.random()] = entitySpawnerData
        entitySpawners.persistentDataEntry.set(persistentDataContainer, spawners)
        update()
    }

    fun removeFromWorld(world: World, key: Uuid) {
        val persistentDataContainer = world.persistentDataContainer
        val spawners = entitySpawners.persistentDataEntry.getOrDefault(
            persistentDataContainer, emptyMap()
        ).toMutableMap()
        if(key !in spawners.keys) return
        spawners.remove(key)
        entitySpawners.persistentDataEntry.set(persistentDataContainer, spawners)
        update()
    }

    fun editSpawner(world: World, key: Uuid, edit: EntitySpawnerData.Builder.() -> Unit) {
        val persistentDataContainer = world.persistentDataContainer
        val spawners = entitySpawners.persistentDataEntry.getOrDefault(
            persistentDataContainer, emptyMap()
        ).toMutableMap()
        spawners[key] = (spawners[key] ?: return).edit(edit)
        entitySpawners.persistentDataEntry.set(persistentDataContainer, spawners)
        update()
    }

    fun update() {
        cleanup()
        for(world in Bukkit.getWorlds()) {
            val spawners = entitySpawners.persistentDataEntry.getOrDefault(world.persistentDataContainer, emptyMap())
            for((key, spawner) in spawners.entries) {
                createDisplay(world.key to key).apply { prepareDisplay(this, spawner, world, key) }
            }
        }
    }

    private fun prepareDisplay(display: HologramDisplay, spawner: EntitySpawnerData, world: World, key: Uuid) {
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
                line.setBrightness(HologramManager.brightness())
                line.addAction("highlight-spawner",
                    HologramActionTypes.callback().left(
                        HologramCallbackEvent.createCallback(highlightKey, key.toHexDashString())
                    )
                )
                line.addAction("edit-spawner",
                    HologramActionTypes.callback().right(
                        HologramCallbackEvent.createCallback(editKey, key.toHexDashString())
                    )
                )
            }
            display.addHologram(pos.clone().add(0.0, scale.y.toDouble() + 0.5, 0.0)) {
                val line = it.addTextLine()
                line.setText(title)
                line.setBrightness(HologramManager.brightness())
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

    fun getSpawner(world: World, key: Uuid): EntitySpawnerData? {
        val spawners = entitySpawners.persistentDataEntry.getOrDefault(world.persistentDataContainer, emptyMap())
        return spawners[key]
    }

    fun highlightSpawner(player: Player, world: World, key: Uuid) {
        player.playSound(Sounds.HIGHLIGHT_SPAWNER)
        val display = HologramManager.getHologramPlayer(player).createDisplay(highlightKey)
        val spawner = getSpawner(world, key) ?: return

        val entityType = InstanceEditor.infoDump.getEntityType(spawner.entityTypeId)
        val scale = getEntityTypeScale(entityType)

        for(pos in spawner.positions.map { it.zeroRotation.toPaperLocation(world) }) {
            display.addHologram(pos) {
                val line = it.addItemLine()
                line.isGlowing = true
                line.itemStack = ItemStack.of(Material.GLASS)
                line.billboard = org.bukkit.entity.Display.Billboard.FIXED
                line.transformation = TransformationUtils.scale(scale)
                line.setBrightness(HologramManager.brightness())
            }
        }

        Bukkit.getScheduler().runTaskLater(5 * 20L) {
            if(!display.isRemoved) {
                display.remove()
            }
        }
    }

    fun editSpawner(player: Player, world: World, key: Uuid) {
        val spawner = getSpawner(world, key) ?: return
        val entityType = InstanceEditor.infoDump.getEntityType(spawner.entityTypeId)

        val title = getEntityTitle(entityType, spawner.entityTypeId)

        val entityTypeDialog = if(entityType == null) {
            unknownEntityType(world, key)
        } else {
            knownEntityType(world, key)
        }

        player.showDialog(
            dialog(title) {
                body {
                    message(Component.text("Amount of Positions: ${spawner.positions.size}"))
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
                                            removeFromWorld(world, key)
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
                            response, audience -> editSpawner(world, key) {
                                maxSpawned((response.getFloat("max_spawned")?.toInt() ?: 1).coerceAtLeast(1))
                            }
                            audience.closeDialog()
                        }
                    }
                    add(Component.text("Get Position List")) {
                        ClickEvent.callback {
                            val positionList = PositionList()
                            positionList.addPositions(world, spawner.positions)
                            if(it is Player) it.give(positionList.createPositionListItem())
                            it.closeDialog()
                        }.dialog
                    }
                    add(Component.text("Change Type")) {
                        ClickEvent.showDialog(entityTypeDialog).dialog
                    }
                }
            }
        )
    }

    fun knownEntityType(world: World, key: Uuid): Dialog {
        val spawner = getSpawner(world, key) ?: error("This should not happen")
        val entityType = InstanceEditor.infoDump.getEntityType(spawner.entityTypeId)

        val title = getEntityTitle(entityType, spawner.entityTypeId)

        return dialog(title) {
            inputs {
                singleOption("type", Component.text("Entity Type")) {
                    var hasInitial = false
                    for(infoEntity in InstanceEditor.infoDump.entityTypes) {
                        val initial = !hasInitial && infoEntity.id == spawner.entityTypeId
                        if(initial) hasInitial = true
                        add(infoEntity.id, infoEntity.entityName, initial)
                    }
                }
            }
            multiAction {
                add(Component.text("Change")) {
                    @Suppress("UnstableApiUsage")
                    dynamicCallback {
                        response, audience -> editSpawner(world, key) {
                            response.getText("type")?.let { entityTypeId(it) }
                        }
                        audience.closeDialog()
                    }
                }
                add(Component.text("Unknown")) {
                    ClickEvent.callback {
                        it.showDialog(unknownEntityType(world, key))
                    }.dialog
                }
            }
        }
    }

    fun unknownEntityType(world: World, key: Uuid): Dialog {
        val spawner = getSpawner(world, key) ?: error("This should not happen")
        val entityType = InstanceEditor.infoDump.getEntityType(spawner.entityTypeId)

        val title = getEntityTitle(entityType, spawner.entityTypeId)

        return dialog(title) {
            inputs {
                @Suppress("UnstableApiUsage")
                text("type", Component.text("Entity Type")) {
                    initial(spawner.entityTypeId)
                    maxLength(1024)
                }
            }
            multiAction {
                add(Component.text("Change")) {
                    @Suppress("UnstableApiUsage")
                    dynamicCallback {
                        response, audience -> editSpawner(world, key) {
                            response.getText("type")?.let { entityTypeId(it.ifEmpty { "error" }) }
                        }
                        audience.closeDialog()
                    }
                }
                add(Component.text("Known")) {
                    ClickEvent.callback {
                        it.showDialog(knownEntityType(world, key))
                    }.dialog
                }
            }
        }
    }

    @EventHandler
    fun onHologramCallback(event: HologramCallbackEvent) {
        event.checkCallbackCommand(highlightKey, 1)?.let {
            val key = Uuid.parseHexDashOrNull(it[0]) ?: return
            highlightSpawner(event.player, event.line.world, key)
            return
        }
        event.checkCallbackCommand(editKey, 1)?.let {
            val key = Uuid.parseHexDashOrNull(it[0]) ?: return
            editSpawner(event.player, event.line.world, key)
            return
        }
    }
}
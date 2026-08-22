package org.joebobilly.appleattack.events

import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.instance.InstanceRegisterEvent
import net.minestom.server.event.instance.InstanceUnregisterEvent
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagHandler
import org.joebobilly.appleattack.entities.spawners.EntitySpawner
import org.joebobilly.appleattack.entities.spawners.EntitySpawnerData
import org.joebobilly.appleattack.entities.spawners.SpawnerManager
import org.joebobilly.appleattack.serialization.MinestomSerializationEntry.Companion.minestomTag
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes
import org.joebobilly.appleattack.utils.TagUtils.getTagSourced

object InstanceEvents {
    // we are only getting the BukkitValues tag from Minestom's side
    val data: Tag<BinaryTag> = Tag.NBT("Data")
    val persistentData: Tag<BinaryTag> = Tag.NBT("BukkitValues")

    val entitySpawners = SerializationTypes.mapUUID(EntitySpawnerData.Serializer).toEntry("entity_spawners")

    fun init(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(InstanceRegisterEvent::class.java) {
            val data = it.instance.getTag(data) as? CompoundBinaryTag ?: return@addListener
            val handler = TagHandler.fromCompound(persistentData.read(data) as? CompoundBinaryTag ?: return@addListener)

            val entitySpawners = handler.getTagSourced(entitySpawners.minestomTag)
            entitySpawners?.values?.forEach {
                spawner -> SpawnerManager.registerSpawner(EntitySpawner(spawner), it.instance)
            }
        }
        eventHandler.addListener(InstanceUnregisterEvent::class.java) {
            SpawnerManager.unregisterAllInInstance(it.instance)
        }
    }
}
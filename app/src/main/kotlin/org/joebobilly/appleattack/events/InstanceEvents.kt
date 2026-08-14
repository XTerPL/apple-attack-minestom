package org.joebobilly.appleattack.events

import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.instance.InstanceRegisterEvent
import net.minestom.server.event.instance.InstanceUnregisterEvent
import org.joebobilly.appleattack.entities.spawners.EntitySpawner
import org.joebobilly.appleattack.entities.spawners.SpawnerManager
import org.joebobilly.appleattack.serialization.MinestomSerializationEntry.Companion.minestomTag
import org.joebobilly.appleattack.serialization.SerializationType.Companion.list
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.utils.TagUtils.getTagSourced

object InstanceEvents {
    val entitySpawners = EntitySpawner.Serializer.list().toEntry("entity_spawners")

    fun init(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(InstanceRegisterEvent::class.java) {
            val entitySpawners = it.instance.getTagSourced(entitySpawners.minestomTag)
            entitySpawners?.forEach { spawner -> SpawnerManager.registerSpawner(spawner, it.instance) }
        }
        eventHandler.addListener(InstanceUnregisterEvent::class.java) {
            SpawnerManager.unregisterAllInInstance(it.instance)
        }
    }
}
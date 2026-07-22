package org.joebobilly.appleattack.events

import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.instance.InstanceRegisterEvent
import net.minestom.server.event.instance.InstanceUnregisterEvent
import net.minestom.server.tag.Tag
import org.joebobilly.appleattack.entities.spawners.EntitySpawner
import org.joebobilly.appleattack.entities.spawners.SpawnerManager
import org.joebobilly.appleattack.utils.TagUtils.getTagSourced

object InstanceEvents {
    val entitySpawners: Tag<List<EntitySpawner>> = EntitySpawner.tag("entity_spawners").list()

    fun init(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(InstanceRegisterEvent::class.java) {
            val entitySpawners = it.instance.getTagSourced(entitySpawners)
            entitySpawners?.forEach { spawner -> SpawnerManager.registerSpawner(spawner, it.instance) }
        }
        eventHandler.addListener(InstanceUnregisterEvent::class.java) {
            SpawnerManager.unregisterAllInInstance(it.instance)
        }
    }
}
package org.joebobilly.appleattack.entities.spawners

import net.minestom.server.instance.Instance
import net.minestom.server.timer.SchedulerManager
import net.minestom.server.timer.TaskSchedule
import org.joebobilly.appleattack.entities.type.AAMobType
import org.joebobilly.appleattack.entities.type.NPCType
import org.joebobilly.appleattack.utils.Position

object SpawnerManager {
    fun EntitySpawner.Companion.mob(mobType: AAMobType, positions: List<Position>, maxSpawned: Int = 1)
        = EntitySpawner(EntitySpawnerData(mobType.id, positions, maxSpawned))
    fun EntitySpawner.Companion.npc(npcType: NPCType, position: Position)
        = EntitySpawner(EntitySpawnerData(npcType.id, listOf(position)))

    private val spawners = mutableListOf<EntitySpawner>()

    fun tick() {
        spawners.forEach {
            it.trySpawn()
        }
    }

    fun registerSpawner(spawner: EntitySpawner, instance: Instance) {
        if(spawner.isInInstance()) return
        spawner.setInstance(instance)
        spawners.add(spawner)
    }

    fun unregisterSpawner(spawner: EntitySpawner) {
        if(spawners.remove(spawner)) {
            spawner.setInstance(null)
        }
    }

    fun unregisterAllInInstance(instance: Instance) {
        val iterator = spawners.listIterator()
        while (iterator.hasNext()) {
            val spawner = iterator.next()
            if(spawner.getInstance() == instance) {
                spawner.setInstance(null)
                iterator.remove()
            }
        }
    }

    fun init(schedulerManager: SchedulerManager) {
        schedulerManager.scheduleTask(::tick, TaskSchedule.seconds(5), TaskSchedule.seconds(5))
    }
}
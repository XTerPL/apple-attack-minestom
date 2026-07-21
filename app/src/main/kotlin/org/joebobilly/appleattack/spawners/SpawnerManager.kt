package org.joebobilly.appleattack.spawners

import net.minestom.server.instance.Instance
import net.minestom.server.timer.SchedulerManager
import net.minestom.server.timer.TaskSchedule

object SpawnerManager {
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
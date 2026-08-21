package org.joebobilly.appleattack.entities.spawners

import net.minestom.server.entity.Entity
import net.minestom.server.event.entity.EntityDespawnEvent
import net.minestom.server.instance.Instance
import org.joebobilly.appleattack.entities.AAEntityTypeManager
import org.joebobilly.appleattack.entities.type.AAEntityType
import org.joebobilly.appleattack.utils.Position
import org.joebobilly.appleattack.utils.PositionUtils.toMinestomPos
import org.joebobilly.appleattack.utils.RandomUtils.pickFrom
import java.util.logging.Logger
import kotlin.random.Random

class EntitySpawner(val entitySpawnerData: EntitySpawnerData) {
    companion object {
        val logger: Logger = Logger.getLogger("entity-spawners")
    }
    
    val entityType: AAEntityType<*> get() = AAEntityTypeManager.getOrThrow(entitySpawnerData.entityTypeId)
    val positions: List<Position> get() = entitySpawnerData.positions
    val maxSpawned: Int get() = entitySpawnerData.maxSpawned
    
    private var instance: Instance? = null
    private val spawnedEntities = mutableListOf<Entity>()
    
    fun trySpawn() {
        val instance = instance ?: return
        if(!instance.isRegistered) return
        if(spawnedEntities.count() >= maxSpawned) return
        val spawnLocation = Random.pickFrom(positions)
        entityType.spawn(instance, spawnLocation.toMinestomPos())?.apply {
            if(this.instance == null) throw IllegalStateException("You need to set an instance for the spawned entity smh")
            eventNode().addListener(EntityDespawnEvent::class.java) {
                spawnedEntities.remove(this)
            }
            spawnedEntities.add(this)
        }
    }
    fun isInInstance(): Boolean {
        return instance?.isRegistered ?: false
    }
    fun getInstance(): Instance? {
        return instance
    }
    internal fun setInstance(instance: Instance?) {
        spawnedEntities.forEach {
            it.remove()
        }
        spawnedEntities.clear()
        this.instance = instance
    }
}
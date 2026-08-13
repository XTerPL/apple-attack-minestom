package org.joebobilly.appleattack.entities.spawners

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.event.entity.EntityDespawnEvent
import net.minestom.server.instance.Instance
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTReadError
import org.joebobilly.appleattack.serialization.NBTSerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes
import org.joebobilly.appleattack.utils.Position

sealed class EntitySpawner(val maxSpawned: Int = 1) {
    private var instance: Instance? = null
    private val spawnedEntities = mutableListOf<Entity>()

    open fun canSpawn(): Boolean {
        return true
    }
    abstract fun spawnEntity(instance: Instance, spawnLocation: Pos): Entity?
    abstract fun getSpawnLocation(): Position?
    fun trySpawn() {
        val instance = instance ?: return
        if(!instance.isRegistered) return
        if(spawnedEntities.count() >= maxSpawned) return
        if(!canSpawn()) return
        val spawnLocation = getSpawnLocation() ?: return
        spawnEntity(instance, spawnLocation.toMinestomPos())?.apply {
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

    object Serializer : NBTSerializer<EntitySpawner>(EntitySpawner::class) {
        val spawnerType = SerializationTypes.STRING.toEntry("spawner_type")

        override fun read(context: DeserializationContext): EntitySpawner {
            return when(val type = context.read(spawnerType)) {
                "mob" -> MobSpawner.Serializer.read(context)
                "npc" -> NPCSpawner.Serializer.read(context)
                else -> throw NBTReadError("", "Invalid entity spawner type: $type")
            }
        }

        override fun write(context: SerializationContext, value: EntitySpawner) {
            context.write(spawnerType, when(value) {
                is MobSpawner -> {
                    MobSpawner.Serializer.write(context, value)
                    "mob"
                }
                is NPCSpawner -> {
                    NPCSpawner.Serializer.write(context, value)
                    "npc"
                }
            })
        }
    }
}
package org.joebobilly.appleattack.entities.spawners

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.event.entity.EntityDespawnEvent
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagSerializer
import net.minestom.server.tag.TagWritable
import org.joebobilly.appleattack.utils.NBTReadError
import org.joebobilly.appleattack.utils.TagUtils.getTagOrThrow

sealed class EntitySpawner(val maxSpawned: Int = 1) {
    companion object {
        fun tag(key: String): Tag<EntitySpawner> {
            return Tag.Structure(key, Serializer)
        }
    }

    private var instance: Instance? = null
    private val spawnedEntities = mutableListOf<Entity>()

    open fun canSpawn(): Boolean {
        return true
    }
    abstract fun spawnEntity(instance: Instance, spawnLocation: Pos): Entity?
    abstract fun getSpawnLocation(): Pos?
    fun trySpawn() {
        val instance = instance ?: return
        if(!instance.isRegistered) return
        if(spawnedEntities.count() >= maxSpawned) return
        if(!canSpawn()) return
        val spawnLocation = getSpawnLocation() ?: return
        spawnEntity(instance, spawnLocation)?.apply {
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

    object Serializer : TagSerializer<EntitySpawner> {
        val typeTag: Tag<String> = Tag.String("spawner_type")

        override fun read(reader: TagReadable): EntitySpawner {
            return when(val type = reader.getTagOrThrow(typeTag)) {
                "mob" -> MobSpawner.Serializer.read(reader)
                "npc" -> NPCSpawner.Serializer.read(reader)
                else -> throw NBTReadError("", "Invalid entity spawner type: $type")
            }
        }

        override fun write(writer: TagWritable, value: EntitySpawner) {
            writer.setTag(typeTag, when(value) {
                is MobSpawner -> {
                    MobSpawner.Serializer.write(writer, value)
                    "mob"
                }
                is NPCSpawner -> {
                    NPCSpawner.Serializer.write(writer, value)
                    "npc"
                }
            })
        }
    }
}
package org.joebobilly.appleattack.entities.spawners

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance
import org.joebobilly.appleattack.entities.AAEntityTypeManager
import org.joebobilly.appleattack.entities.type.NPCType
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTReadError
import org.joebobilly.appleattack.serialization.NBTSerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.map
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes
import org.joebobilly.appleattack.utils.Position

class NPCSpawner(val npcType: NPCType, val position: Position)
    : EntitySpawner(1) {
    override fun getSpawnLocation(): Position {
        return position
    }
    override fun spawnEntity(instance: Instance, spawnLocation: Pos): Entity? {
        return npcType.spawn(instance, spawnLocation)
    }

    object Serializer : NBTSerializer<NPCSpawner> {
        val npcType = SerializationTypes.STRING.map(
            {
                val type = AAEntityTypeManager.getOrThrow(it)
                type as? NPCType ?: throw NBTReadError("", "$type is not a npc type!")
            }, NPCType::id
        ).toEntry("id")
        val position = SerializationTypes.POSITION.toEntry("position")

        override val klass = NPCSpawner::class

        override fun read(context: DeserializationContext): NPCSpawner {
            val type = context.read(npcType)
            val position = context.read(position)
            return NPCSpawner(type, position)
        }

        override fun write(context: SerializationContext, value: NPCSpawner) {
            context.write(npcType, value.npcType)
            context.write(position, value.position)
        }
    }
}
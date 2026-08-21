package org.joebobilly.appleattack.entities.spawners

import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTReadError
import org.joebobilly.appleattack.serialization.NBTSerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.list
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes
import org.joebobilly.appleattack.utils.Position

data class EntitySpawnerData(
    val entityTypeId: String,
    val positions: List<Position>,
    val maxSpawned: Int = 1
) {
    init {
        require(maxSpawned > 0) { "maxSpawned must be greater than 0" }
        require(positions.isNotEmpty()) { "Spawning positions must be given." }
    }

    object Serializer : NBTSerializer<EntitySpawnerData> {
        val entityTypeId = SerializationTypes.STRING.toEntry("id")
        val positions = SerializationTypes.POSITION.list().toEntry("positions")
        val maxSpawned = SerializationTypes.INTEGER.toEntry("max_spawned")

        override val klass = EntitySpawnerData::class

        override fun read(context: DeserializationContext): EntitySpawnerData {
            val type = context.read(entityTypeId)
            val maxSpawned = context.read(maxSpawned)
            NBTReadError.checkOrThrow(maxSpawned > 0, "max_spawned") { "max_spawned has to be greater than 0" }
            val positions = context.read(positions)
            NBTReadError.checkOrThrow(positions.isNotEmpty(), "positions") {
                "there has to be at least one entry in positions"
            }
            return EntitySpawnerData(type, positions, maxSpawned)
        }

        override fun write(context: SerializationContext, value: EntitySpawnerData) {
            context.write(entityTypeId, value.entityTypeId)
            context.write(positions, value.positions)
            context.write(maxSpawned, value.maxSpawned)
        }
    }
}
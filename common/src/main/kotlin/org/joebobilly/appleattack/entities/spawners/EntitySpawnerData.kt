package org.joebobilly.appleattack.entities.spawners

import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
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
    companion object {
        fun builder(init: Builder.() -> Unit): EntitySpawnerData {
            return Builder().apply(init).build()
        }
    }

    init {
        require(maxSpawned > 0) { "maxSpawned must be greater than 0" }
        require(positions.isNotEmpty()) { "Spawning positions must be given." }
    }

    fun toBuilder(): Builder {
        val builder = Builder()
        builder.entityTypeId(entityTypeId)
        builder.addPositions(positions)
        builder.maxSpawned(maxSpawned)
        return builder
    }

    fun edit(edit: Builder.() -> Unit): EntitySpawnerData {
        return toBuilder().apply(edit).build()
    }

    class Builder {
        private var entityTypeId: String? = null
        private val positions = mutableListOf<Position>()
        private var maxSpawned = 1

        fun entityTypeId(entityTypeId: String) = this.apply {
            this.entityTypeId = entityTypeId
        }

        fun addPosition(position: Position) = this.apply {
            this.positions.add(position)
        }
        fun addPositions(positions: List<Position>) = this.apply {
            this.positions.addAll(positions)
        }

        fun maxSpawned(maxSpawned: Int) = this.apply {
            require(maxSpawned > 0) { "maxSpawned must be greater than 0" }
            this.maxSpawned = maxSpawned
        }

        fun build() = EntitySpawnerData(
            checkNotNull(entityTypeId) { "entityTypeId can not be null" },
            positions.toList(), maxSpawned
        )
    }

    object Serializer : NBTSerializer<EntitySpawnerData> {
        val entityTypeId = SerializationTypes.STRING.toEntry("id")
        val positions = SerializationTypes.POSITION.list().toEntry("positions")
        val maxSpawned = SerializationTypes.INTEGER.toEntry("max_spawned")

        override val klass = EntitySpawnerData::class

        override fun read(context: DeserializationContext): EntitySpawnerData {
            return builder {
                entityTypeId(context.read(entityTypeId))
                maxSpawned(context.read(maxSpawned))
                addPositions(context.read(positions))
            }
        }

        override fun write(context: SerializationContext, value: EntitySpawnerData) {
            context.write(entityTypeId, value.entityTypeId)
            context.write(positions, value.positions)
            context.write(maxSpawned, value.maxSpawned)
        }
    }
}
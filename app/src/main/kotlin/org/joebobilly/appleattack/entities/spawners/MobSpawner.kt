package org.joebobilly.appleattack.entities.spawners

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance
import org.joebobilly.appleattack.entities.AAEntityTypeManager
import org.joebobilly.appleattack.entities.type.AAMobType
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTReadError
import org.joebobilly.appleattack.serialization.NBTSerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.list
import org.joebobilly.appleattack.serialization.SerializationType.Companion.map
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes
import org.joebobilly.appleattack.utils.Position
import org.joebobilly.appleattack.utils.RandomUtils.pickFrom
import kotlin.random.Random

class MobSpawner(val mobType: AAMobType, maxSpawned: Int, val positions: List<Position>)
    : EntitySpawner(maxSpawned) {
    init {
        require(maxSpawned > 0) { "maxSpawned must be greater than 0" }
        require(positions.isNotEmpty()) { "Spawning positions must be given." }
    }

    override fun getSpawnLocation(): Position {
        return Random.pickFrom(positions)
    }
    override fun spawnEntity(instance: Instance, spawnLocation: Pos): Entity? {
        return mobType.spawn(instance, spawnLocation)
    }

    object Serializer : NBTSerializer<MobSpawner> {
        val mobType = SerializationTypes.STRING.map(
            {
                val type = AAEntityTypeManager.getOrThrow(it)
                type as? AAMobType ?: throw NBTReadError("", "$type is not a mob type!")
            }, AAMobType::id
        ).toEntry("id")
        val maxSpawned = SerializationTypes.INTEGER.toEntry("max_spawned")
        val positions = SerializationTypes.POSITION.list().toEntry("positions")

        override val klass = MobSpawner::class

        override fun read(context: DeserializationContext): MobSpawner {
            val type = context.read(mobType)
            val maxSpawned = context.read(maxSpawned)
            NBTReadError.checkOrThrow(maxSpawned > 0, "max_spawned") { "max_spawned has to be greater than 0" }
            val positions = context.read(positions)
            NBTReadError.checkOrThrow(positions.isNotEmpty(), "positions") {
                "there has to be at least one entry in positions"
            }
            return MobSpawner(type, maxSpawned, positions)
        }

        override fun write(context: SerializationContext, value: MobSpawner) {
            context.write(mobType, value.mobType)
            context.write(maxSpawned, value.maxSpawned)
            context.write(positions, value.positions)
        }
    }
}
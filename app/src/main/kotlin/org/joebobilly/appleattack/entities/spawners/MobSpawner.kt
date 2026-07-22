package org.joebobilly.appleattack.entities.spawners

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagSerializer
import net.minestom.server.tag.TagWritable
import org.joebobilly.appleattack.entities.AAEntityTypeManager
import org.joebobilly.appleattack.entities.type.AAMobType
import org.joebobilly.appleattack.utils.NBTReadError
import org.joebobilly.appleattack.utils.RandomUtils.pickFrom
import org.joebobilly.appleattack.utils.TagUtils
import org.joebobilly.appleattack.utils.TagUtils.getTagOrThrow
import kotlin.random.Random

class MobSpawner(val mobType: AAMobType, maxSpawned: Int, val positions: List<Pos>)
    : EntitySpawner(maxSpawned) {
    init {
        require(maxSpawned > 0) { "maxSpawned must be greater than 0" }
        require(positions.isNotEmpty()) { "Spawning positions must be given." }
    }

    override fun getSpawnLocation(): Pos {
        return Random.pickFrom(positions)
    }
    override fun spawnEntity(instance: Instance, spawnLocation: Pos): Entity? {
        return mobType.spawn(instance, spawnLocation)
    }

    object Serializer : TagSerializer<MobSpawner> {
        val mobType: Tag<AAMobType> = Tag.String("id").map(
            {
                val type = AAEntityTypeManager.get(it)
                type as? AAMobType ?: throw NBTReadError("", "$type is not a mob type!")
            }, AAMobType::id
        )
        val maxSpawned: Tag<Int> = Tag.Integer("max_spawned")
        val positions: Tag<List<Pos>> = TagUtils.posTag("positions").list()

        override fun read(reader: TagReadable): MobSpawner {
            val type = reader.getTagOrThrow(mobType)
            val maxSpawned = reader.getTagOrThrow(maxSpawned)
            TagUtils.checkOrThrow(maxSpawned > 0, "max_spawned") { "max_spawned has to be greater than 0" }
            val positions = reader.getTagOrThrow(positions)
            TagUtils.checkOrThrow(positions.isNotEmpty(), "positions") {
                "there has to be at least one entry in positions"
            }
            return MobSpawner(type, maxSpawned, positions)
        }

        override fun write(writer: TagWritable, value: MobSpawner) {
            writer.setTag(mobType, value.mobType)
            writer.setTag(maxSpawned, value.maxSpawned)
            writer.setTag(positions, value.positions)
        }
    }
}
package org.joebobilly.appleattack.entities.spawners

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagSerializer
import net.minestom.server.tag.TagWritable
import org.joebobilly.appleattack.entities.AAEntityTypeManager
import org.joebobilly.appleattack.entities.type.NPCType
import org.joebobilly.appleattack.utils.NBTReadError
import org.joebobilly.appleattack.utils.TagUtils
import org.joebobilly.appleattack.utils.TagUtils.getTagOrThrow

class NPCSpawner(val npcType: NPCType, val position: Pos)
    : EntitySpawner(1) {
    override fun getSpawnLocation(): Pos {
        return position
    }
    override fun spawnEntity(instance: Instance, spawnLocation: Pos): Entity? {
        return npcType.spawn(instance, spawnLocation)
    }

    object Serializer : TagSerializer<NPCSpawner> {
        val npcType: Tag<NPCType> = Tag.String("id").map(
            {
                val type = AAEntityTypeManager.get(it)
                type as? NPCType ?: throw NBTReadError("", "$type is not a npc type!")
            }, NPCType::id
        )
        val position = TagUtils.posTag("position")

        override fun read(reader: TagReadable): NPCSpawner {
            val type = reader.getTagOrThrow(npcType)
            val position = reader.getTagOrThrow(position)
            return NPCSpawner(type, position)
        }

        override fun write(writer: TagWritable, value: NPCSpawner) {
            writer.setTag(npcType, value.npcType)
            writer.setTag(position, value.position)
        }
    }
}
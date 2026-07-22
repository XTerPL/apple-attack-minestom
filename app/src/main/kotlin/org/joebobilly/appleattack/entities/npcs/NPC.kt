package org.joebobilly.appleattack.entities.npcs

import net.minestom.server.entity.Entity
import org.joebobilly.appleattack.entities.type.NPCType

class NPC internal constructor(val type: NPCType) : Entity(type.startingEntityType) {
    override fun update(time: Long) {
        super.update(time)
        if(type.shouldDespawn(instance, position)) {
            remove()
        }
    }
}
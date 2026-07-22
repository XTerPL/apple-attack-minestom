package org.joebobilly.appleattack.entities

import net.minestom.server.entity.Entity
import org.joebobilly.appleattack.entities.mobs.AAMob
import org.joebobilly.appleattack.entities.npcs.NPC
import org.joebobilly.appleattack.entities.type.AAEntityType
import org.joebobilly.appleattack.utils.ValueRegistry

object AAEntityTypeManager : ValueRegistry<AAEntityType<*>>(AAEntityType<*>::id, "entity type") {
    fun getEntityType(entity: Entity): AAEntityType<*>? {
        if(entity is AAMob) return entity.type
        if(entity is NPC) return entity.type
        return null
    }
}
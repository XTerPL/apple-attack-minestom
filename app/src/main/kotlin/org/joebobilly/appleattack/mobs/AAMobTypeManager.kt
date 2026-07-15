package org.joebobilly.appleattack.mobs

import net.minestom.server.entity.Entity
import org.joebobilly.appleattack.utils.ValueRegistry

object AAMobTypeManager : ValueRegistry<AAMobType>(AAMobType::id, "mob type") {
    fun getMobType(entity: Entity): AAMobType? {
        if(entity is AAMob) return entity.type
        return null
    }
}
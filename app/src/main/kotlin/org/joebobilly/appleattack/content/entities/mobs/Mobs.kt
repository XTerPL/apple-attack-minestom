package org.joebobilly.appleattack.content.entities.mobs

import org.joebobilly.appleattack.entities.AAEntityTypeManager

object Mobs {
    fun register() {
        AAEntityTypeManager.register(AppleMob)
    }
}
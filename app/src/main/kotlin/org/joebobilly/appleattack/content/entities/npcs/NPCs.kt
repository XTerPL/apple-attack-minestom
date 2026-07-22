package org.joebobilly.appleattack.content.entities.npcs

import org.joebobilly.appleattack.entities.AAEntityTypeManager

object NPCs {
    fun register() {
        AAEntityTypeManager.register(TestNPC)
    }
}
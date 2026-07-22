package org.joebobilly.appleattack.entities.type

import net.minestom.server.entity.EntityType
import org.joebobilly.appleattack.entities.npcs.NPC
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.players.cutscenes.Cutscene

abstract class NPCType(id: String, startingEntityType: EntityType) : AAEntityType<NPC>(id, startingEntityType) {
    final override fun createUninitialized(): NPC {
        return NPC(this)
    }

    open fun initCutscene(entity: NPC, player: AAPlayer, cutscene: Cutscene) {

    }
}
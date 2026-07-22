package org.joebobilly.appleattack.events

import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerEntityInteractEvent
import org.joebobilly.appleattack.entities.npcs.NPC
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.players.cutscenes.Cutscene

object InteractEvents {
    fun init(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(PlayerEntityInteractEvent::class.java) {
            val player = it.player as? AAPlayer ?: return@addListener
            val npc = it.target as? NPC ?: return@addListener
            val cutscene = Cutscene()
            npc.type.initCutscene(npc, player, cutscene)
            cutscene.start(player)
        }
    }
}
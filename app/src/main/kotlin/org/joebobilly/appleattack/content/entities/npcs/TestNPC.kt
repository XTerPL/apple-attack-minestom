package org.joebobilly.appleattack.content.entities.npcs

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.VillagerProfession
import net.minestom.server.entity.VillagerType
import net.minestom.server.entity.metadata.villager.VillagerMeta
import net.minestom.server.entity.metadata.villager.VillagerMeta.Level
import org.joebobilly.appleattack.entities.npcs.NPC
import org.joebobilly.appleattack.entities.type.NPCType
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.players.cutscenes.Cutscene

object TestNPC : NPCType("test_npc", EntityType.VILLAGER) {
    override fun entityName(): Component = Component.text("Test NPC", NamedTextColor.GOLD)

    override fun onInit(entity: NPC) {
        entity.editEntityMeta(VillagerMeta::class.java) {
            it.villagerData = VillagerMeta.VillagerData(
                VillagerType.PLAINS, VillagerProfession.NITWIT, Level.NOVICE
            )
        }
    }

    override fun initCutscene(entity: NPC, player: AAPlayer, cutscene: Cutscene) {
        cutscene.speak("Hello and welcome to the test NPC!").waitSeconds(3)
        cutscene.speak(":)")
    }
}
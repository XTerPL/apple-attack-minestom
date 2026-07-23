package org.joebobilly.appleattack.content.entities.npcs

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.VillagerProfession
import net.minestom.server.entity.VillagerType
import net.minestom.server.entity.metadata.villager.VillagerMeta
import net.minestom.server.entity.metadata.villager.VillagerMeta.Level
import net.minestom.server.inventory.InventoryType
import org.joebobilly.appleattack.content.items.AppleItem
import org.joebobilly.appleattack.content.items.tools.swords.StarterSword
import org.joebobilly.appleattack.entities.npcs.NPC
import org.joebobilly.appleattack.entities.type.NPCType
import org.joebobilly.appleattack.interfaces.UserInterface
import org.joebobilly.appleattack.interfaces.shop.ShopSlot
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.players.cutscenes.Cutscene
import org.joebobilly.appleattack.rewards.Cost
import org.joebobilly.appleattack.rewards.Reward
import org.joebobilly.appleattack.rewards.Transaction

object TestNPC : NPCType("test_npc", EntityType.VILLAGER) {
    val starterSwordWare = Transaction(
        Reward.Item.of(StarterSword, ToolMeta()),
        Cost.Builder().addCost(AppleItem, 16).build()
    )

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
        cutscene.openInterface { object : UserInterface(InventoryType.CHEST_3_ROW, "Test NPC") {
            override fun defineSlots(definer: SlotDefiner) {
                definer.setSlot(4, 1, ShopSlot(starterSwordWare))
            }
        } }
    }
}
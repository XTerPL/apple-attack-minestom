package org.joebobilly.appleattack.content.mobs

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.entity.ai.goal.MeleeAttackGoal
import net.minestom.server.entity.ai.target.ClosestEntityTarget
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.entity.metadata.avatar.MannequinMeta
import net.minestom.server.network.player.ResolvableProfile
import org.joebobilly.appleattack.content.items.AppleItem
import org.joebobilly.appleattack.damage.AttackInfo
import org.joebobilly.appleattack.mobs.AAMob
import org.joebobilly.appleattack.mobs.AAMobType
import org.joebobilly.appleattack.mobs.HomeStrollGoal
import org.joebobilly.appleattack.rewards.LootTable
import org.joebobilly.appleattack.rewards.Reward
import org.joebobilly.appleattack.rewards.Reward.Companion.toLootTableEntry
import java.time.Duration

object AppleMob : AAMobType("apple", EntityType.MANNEQUIN) {
    val profile = ResolvableProfile(PlayerSkin.fromUsername("LooserRIP"))

    override fun entityName() = Component.text("Apple", NamedTextColor.RED)
    override fun maxHealth() = 5.0
    override fun meleeAttack(entity: AAMob): AttackInfo = AttackInfo.melee(3.0)

    override fun onInit(entity: AAMob) {
        entity.editEntityMeta(MannequinMeta::class.java) {
            it.profile = profile
            it.description = null
        }
        entity.addAIGroup(listOf(
            MeleeAttackGoal(entity, 1.5, Duration.ofSeconds(1)),
            HomeStrollGoal(entity, 8) { entity.homePosition }
        ), listOf(
            ClosestEntityTarget(entity, 10.0) {
                it is Player
            }
        ))
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.23
    }

    override fun onKill(entity: AAMob): LootTable {
        return LootTable()
            .addEntry(
                Reward.Item.of(AppleItem).toLootTableEntry(3, 5)
            )
    }
}
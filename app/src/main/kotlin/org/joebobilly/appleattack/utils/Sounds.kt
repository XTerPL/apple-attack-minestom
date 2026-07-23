package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

object Sounds {
    val BLOCKED_OUTPUT = Sound.sound(Key.key("minecraft:entity.shulker.hurt_closed"), Sound.Source.MASTER, 1f, 1f)
    val FORGE_SUCCEED = Sound.sound(Key.key("minecraft:block.anvil.use"), Sound.Source.MASTER, 1f, 2f)
    val UPGRADE_SUCCEED = Sound.sound(Key.key("minecraft:block.smithing_table.use"), Sound.Source.MASTER, 1f, 1f)

    // generic
    val GENERIC_HURT = Sound.sound(Key.key("entity.generic.hurt"), Sound.Source.HOSTILE, 1f, 1f)
    val GENERIC_DEATH = Sound.sound(Key.key("entity.generic.death"), Sound.Source.HOSTILE, 1f, 1f)
    val GENERIC_TALK = Sound.sound(Key.key("minecraft:entity.villager.ambient"), Sound.Source.NEUTRAL, 1f, 1.5f)
    val GENERIC_PURCHASE = Sound.sound(Key.key("minecraft:entity.villager.yes"), Sound.Source.NEUTRAL, 1f, 1.5f)
    val GENERIC_REFUSE = Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.NEUTRAL, 1f, 1.5f)
}
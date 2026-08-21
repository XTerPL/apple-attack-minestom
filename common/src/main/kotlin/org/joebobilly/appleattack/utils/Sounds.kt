package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

object Sounds {
    val BLOCKED = Sound.sound(Key.key("entity.shulker.hurt_closed"), Sound.Source.MASTER, 1f, 1f)
    val FORGE_SUCCEED = Sound.sound(Key.key("block.anvil.use"), Sound.Source.MASTER, 1f, 2f)
    val UPGRADE_SUCCEED = Sound.sound(Key.key("block.smithing_table.use"), Sound.Source.MASTER, 1f, 1f)
    val REINFORCE_SUCCEED = Sound.sound(Key.key("block.anvil.use"), Sound.Source.MASTER, 1f, 1.5f)

    // generic
    val GENERIC_HURT = Sound.sound(Key.key("entity.generic.hurt"), Sound.Source.HOSTILE, 1f, 1f)
    val GENERIC_DEATH = Sound.sound(Key.key("entity.generic.death"), Sound.Source.HOSTILE, 1f, 1f)
    val GENERIC_TALK = Sound.sound(Key.key("entity.villager.ambient"), Sound.Source.NEUTRAL, 1f, 1.5f)
    val GENERIC_PURCHASE = Sound.sound(Key.key("entity.villager.yes"), Sound.Source.NEUTRAL, 1f, 1.5f)
    val GENERIC_REFUSE = Sound.sound(Key.key("entity.villager.no"), Sound.Source.NEUTRAL, 1f, 1.5f)

    // instance editor
    val POSITION_ADD = Sound.sound(Key.key("block.note_block.snare"), Sound.Source.MASTER, 1f, 1f)
    val POSITION_REMOVE = Sound.sound(Key.key("block.amethyst_cluster.break"), Sound.Source.MASTER, 1f, 1f)
    val HIGHLIGHT_SPAWNER = Sound.sound(Key.key("entity.splash_potion.break"), Sound.Source.MASTER, 1f, 0.5f)
}
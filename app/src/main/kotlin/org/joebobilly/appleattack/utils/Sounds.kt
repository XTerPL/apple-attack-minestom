package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

object Sounds {
    val BLOCKED_OUTPUT = Sound.sound(Key.key("minecraft:entity.shulker.hurt_closed"), Sound.Source.MASTER, 1f, 1f)
    val FORGE_SUCCEED = Sound.sound(Key.key("minecraft:block.anvil.use"), Sound.Source.MASTER, 1f, 2f)
    val UPGRADE_SUCCEED = Sound.sound(Key.key("minecraft:block.smithing_table.use"), Sound.Source.MASTER, 1f, 1f)
}
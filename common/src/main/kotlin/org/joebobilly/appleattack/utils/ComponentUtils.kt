package org.joebobilly.appleattack.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

object ComponentUtils {
    fun Component.toMiniMessage(): String {
        return MiniMessage.miniMessage().serialize(this)
    }
    fun fromMiniMessage(message: String): Component {
        return MiniMessage.miniMessage().deserialize(message)
    }
}
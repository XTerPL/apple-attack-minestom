package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey

object NamespacedKeyUtils {
    fun Key.toNamespacedKey() : NamespacedKey {
        if(this is NamespacedKey) return this
        return NamespacedKey(namespace(), value())
    }
}
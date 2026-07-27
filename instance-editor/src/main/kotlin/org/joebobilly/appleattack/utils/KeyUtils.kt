package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey

object KeyUtils {
    fun Key.toNamespacedKey() : NamespacedKey {
        if(this is NamespacedKey) return this
        return NamespacedKey(namespace(), value())
    }
}
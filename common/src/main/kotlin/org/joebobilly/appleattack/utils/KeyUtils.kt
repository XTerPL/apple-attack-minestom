package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey

object KeyUtils {
    const val NAMESPACE = "apple_attack"
    fun of(value: String): Key = Key.key(NAMESPACE, value)

    fun Key.toNamespacedKey() : NamespacedKey {
        if(this is NamespacedKey) return this
        return NamespacedKey(namespace(), value())
    }
}
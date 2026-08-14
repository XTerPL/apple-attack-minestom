package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.Key

object KeyUtils {
    const val NAMESPACE = "apple_attack"
    fun of(value: String): Key = Key.key(NAMESPACE, value)
}
package org.joebobilly.appleattack

import net.kyori.adventure.key.Key
import org.joebobilly.appleattack.serialization.SerializationEntry
import org.joebobilly.appleattack.serialization.SerializationType

interface Platform {
    companion object {
        private var platform: Platform? = null
        fun setPlatform(platform: Platform) {
            check(this.platform == null) { "Platform was already set!" }
            this.platform = platform
        }
        val instance get() = platform ?: error("Platform wasn't set yet!")
    }

    fun <T : Any> buildSerializationEntry(key: Key, type: SerializationType<T>): SerializationEntry<T>
}
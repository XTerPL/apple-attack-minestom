package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.Key
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType

object KeyPersistentType : PersistentDataType<String, Key> {
    override fun getPrimitiveType() = String::class.java
    override fun getComplexType() = Key::class.java

    override fun toPrimitive(value: Key, ctx: PersistentDataAdapterContext): String {
        return value.asMinimalString()
    }
    override fun fromPrimitive(value: String, ctx: PersistentDataAdapterContext): Key {
        return Key.key(value)
    }
}
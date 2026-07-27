package org.joebobilly.appleattack.utils

import io.papermc.paper.persistence.PersistentDataContainerView
import net.kyori.adventure.key.Key
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.joebobilly.appleattack.InstanceEditor
import org.joebobilly.appleattack.utils.KeyUtils.toNamespacedKey

data class PersistentDataEntry<P : Any, C : Any>(val key: Key, val type: PersistentDataType<P, C>) {
    constructor(key: String, type: PersistentDataType<P, C>) : this(InstanceEditor.key(key), type)

    fun get(container: PersistentDataContainerView): C? {
        return container.get(key.toNamespacedKey(), type)
    }
    fun getOrDefault(container: PersistentDataContainerView, defaultValue: C): C {
        return container.getOrDefault(key.toNamespacedKey(), type, defaultValue)
    }
    fun has(container: PersistentDataContainerView, checkType: Boolean = true): Boolean {
        if(checkType) {
            return container.has(key.toNamespacedKey(), type)
        }
        return container.has(key.toNamespacedKey())
    }
    fun set(container: PersistentDataContainer, value: C) {
        container.set(key.toNamespacedKey(), type, value)
    }
    fun remove(container: PersistentDataContainer) {
        container.remove(key.toNamespacedKey())
    }
}
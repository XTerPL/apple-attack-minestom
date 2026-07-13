package org.joebobilly.appleattack.items

import net.minestom.server.command.builder.suggestion.SuggestionEntry
import java.util.*

object AAItemManager {
    private var frozen = false
    private var items = mutableMapOf<String, AAItem<*>>()

    fun register(item: AAItem<*>) {
        if(frozen) {
            throw IllegalStateException("Can't register an item type after server startup.")
        }
        if(items.containsKey(item.id)) {
            throw IllegalStateException("Cannot register two item types of id ${item.id}.")
        }
        println("Registering item ${item.id}")
        items[item.id] = item
    }
    internal fun isFrozen(): Boolean {
        return frozen
    }
    fun freeze() {
        if(frozen) {
            throw IllegalStateException("Can't freeze the item registry twice.")
        }
        items = Collections.unmodifiableMap(items)
    }
    fun getItem(id: String): AAItem<*>? {
        if(items.containsKey(id)) {
            return items[id]
        }
        return null
    }
    fun getSuggestions(input: String): List<SuggestionEntry> {
        return items.keys.toList().filter { key -> key.startsWith(input) }.map { key -> SuggestionEntry(key) }
    }
}
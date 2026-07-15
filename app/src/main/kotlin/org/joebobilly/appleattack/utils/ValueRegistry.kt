package org.joebobilly.appleattack.utils

import net.minestom.server.command.builder.suggestion.SuggestionEntry
import java.util.Collections

abstract class ValueRegistry<T>(val idProvider: (T) -> String, val name: String) {
    private var frozen = false
    private var values = mutableMapOf<String, T>()

    fun register(value: T) {
        if(frozen) {
            throw IllegalStateException("Can't register an $name after server startup.")
        }
        val id = idProvider(value)
        if(values.containsKey(id)) {
            throw IllegalStateException("Cannot register two ${name}s of id $id.")
        }
        println("Registering $name $id")
        values[id] = value
    }
    fun isFrozen(): Boolean {
        return frozen
    }
    fun freeze() {
        if(frozen) {
            throw IllegalStateException("Can't freeze the $name registry twice.")
        }
        values = Collections.unmodifiableMap(values)
    }
    fun get(id: String): T? {
        if(values.containsKey(id)) {
            return values[id]
        }
        return null
    }
    fun getSuggestions(input: String): List<SuggestionEntry> {
        return values.keys.toList().filter { key -> key.lowercase().startsWith(input.lowercase()) }.map { key -> SuggestionEntry(key) }
    }
}
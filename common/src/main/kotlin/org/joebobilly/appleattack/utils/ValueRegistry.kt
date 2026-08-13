package org.joebobilly.appleattack.utils

import java.util.Collections

abstract class ValueRegistry<T>(val idProvider: (T) -> String, val name: String) {
    private var frozen = false
    private var values = mutableMapOf<String, T>()

    fun register(value: T) {
        throwIfFrozen { "Can't register an $name after server startup." }
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
    fun throwIfFrozen(lazyMessage: () -> Any = { "$name registry must not be frozen" }) {
        if(isFrozen()) throw IllegalStateException(lazyMessage().toString())
    }
    fun freeze() {
        throwIfFrozen { "Can't freeze the $name registry twice." }
        values = Collections.unmodifiableMap(values)
    }
    fun get(id: String): T? {
        if(values.containsKey(id)) {
            return values[id]
        }
        return null
    }
    fun getOrThrow(id: String): T {
        return get(id) ?: throw NoSuchElementException("Unknown $name '$id'!")
    }
    fun <T> getSuggestions(input: String, suggestionEntryFactory: (String) -> T): List<T> {
        return values.keys.toList().filter { key -> key.lowercase().startsWith(input.lowercase()) }.map(suggestionEntryFactory)
    }
}
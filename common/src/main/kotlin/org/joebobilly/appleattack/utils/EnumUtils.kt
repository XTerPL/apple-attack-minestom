package org.joebobilly.appleattack.utils

import java.util.EnumSet

object EnumUtils {
    inline fun <reified T : Enum<T>> emptySet(): EnumSet<T> {
        return EnumSet.noneOf(T::class.java)
    }
    inline fun <reified T : Enum<T>> fullSet(): EnumSet<T> {
        return EnumSet.allOf(T::class.java)
    }
    inline fun <reified T : Enum<T>> setOf(first: T, vararg rest: T): EnumSet<T> {
        return EnumSet.of(first, *rest)
    }
}
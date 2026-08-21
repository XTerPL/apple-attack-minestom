package org.joebobilly.appleattack.utils

import java.util.EnumSet
import kotlin.reflect.KClass

object EnumUtils {
    fun <T : Enum<T>> emptySet(klass: KClass<T>): EnumSet<T> = EnumSet.noneOf(klass.java)
    fun <T : Enum<T>> fullSet(klass: KClass<T>): EnumSet<T> = EnumSet.allOf(klass.java)
    fun <T : Enum<T>> copyOf(klass: KClass<T>, c: Collection<T>) = if(c.isEmpty()) emptySet(klass) else EnumSet.copyOf(c)!!

    inline fun <reified T : Enum<T>> emptySet() = emptySet(T::class)
    inline fun <reified T : Enum<T>> fullSet() = fullSet(T::class)
    inline fun <reified T : Enum<T>> copyOf(c: Collection<T>) = copyOf(T::class, c)
    fun <T : Enum<T>> setOf(first: T, vararg rest: T): EnumSet<T> {
        return EnumSet.of(first, *rest)
    }
}
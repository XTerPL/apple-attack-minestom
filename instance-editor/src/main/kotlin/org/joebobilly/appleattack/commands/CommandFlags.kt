package org.joebobilly.appleattack.commands

import org.joebobilly.appleattack.utils.CollectionUtils.powerSet
import org.joebobilly.appleattack.utils.EnumUtils
import java.util.EnumSet
import kotlin.reflect.KClass

class CommandFlags<T : Enum<T>> private constructor(private val charToFlagMap: Map<Char, T>, private val klass: KClass<T>) {
    companion object {
        fun <T : Enum<T>> of(klass: KClass<T>, vararg flags: Pair<Char, T>): CommandFlags<T> {
            return CommandFlags(mapOf(*flags), klass)
        }
        inline fun <reified T : Enum<T>> of(vararg flags: Pair<Char, T>): CommandFlags<T> {
            return of(T::class, *flags)
        }
        inline fun <reified T : Enum<T>> of(charSupplier: (T) -> Char): CommandFlags<T> {
            return of(*enumValues<T>().map { charSupplier(it) to it }.toTypedArray())
        }
    }
    fun getFlagList(arg: String): List<T>? {
        if(!arg.startsWith('-')) return null
        val list = mutableListOf<T>()
        for(char in arg.substring(1)) {
            charToFlagMap[char]?.apply {
                list.add(this)
            }
        }
        return list.toList()
    }
    fun getFlags(arg: String): EnumSet<T>? {
        return getFlagList(arg)?.let { EnumUtils.copyOf(klass, it) }
    }
    fun getFlagArg(flags: Collection<T>?): String {
        if(flags.isNullOrEmpty()) return ""
        val builder = StringBuilder("-")
        for(flag in flags) {
            builder.append(charToFlagMap.filter { it.value == flag }.keys.firstOrNull() ?: "")
        }
        return builder.toString()
    }
    fun suggest(input: String): List<String> {
        val current = getFlagList(input)?.toMutableList() ?: mutableListOf()

        val availablePowerSet = EnumSet.complementOf(EnumUtils.copyOf(klass, current)).powerSet()

        val outputs = mutableListOf<String>()
        for(available in availablePowerSet) {
            val trying = current.toMutableList()
            trying.addAll(available)
            if(available.isEmpty()) {
                continue
            }
            outputs.add(getFlagArg(trying))
        }
        return outputs.toList()
    }
}
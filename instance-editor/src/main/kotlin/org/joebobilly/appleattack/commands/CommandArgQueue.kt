package org.joebobilly.appleattack.commands

class CommandArgQueue(private val args: Array<out String>) {
    private var index = 0

    fun <T> read(readMap: (String) -> T?): T? {
        if(index !in args.indices) return null
        val value = readMap(args[index])
        if(value != null) index++
        return value
    }

    fun previous(): String? {
        val prev = index - 1
        if(prev !in args.indices) return null
        return args[prev-1]
    }

    fun readString() = read { it }
    fun readInt() = read { it.toIntOrNull() }
    fun <T : Enum<T>> readFlags(commandFlags: CommandFlags<T>) = read { commandFlags.getFlags(it) }
}
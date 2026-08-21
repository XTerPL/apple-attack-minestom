package org.joebobilly.appleattack.holograms

import net.kyori.adventure.key.Key
import net.thenextlvl.hologram.line.HologramLine
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.joebobilly.appleattack.utils.StringUtils.joinWithEscape
import org.joebobilly.appleattack.utils.StringUtils.splitEscaped

class HologramCallbackEvent(val line: HologramLine, val player: Player, callbackString: String) : Event() {
    private val callbackParts = callbackString.splitEscaped()

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlerList

        fun createCallback(key: Key, vararg args: String): String
            = listOf(key.namespace(), key.value(), *args).joinWithEscape()
    }

    override fun getHandlers() = getHandlerList()

    fun checkCallbackCommand(commandKey: Key, argCount: IntRange): List<String>? {
        if(callbackParts.size < 2 || (callbackParts.size - 2) !in argCount) return null
        if(callbackParts[0] != commandKey.namespace()) return null
        if(callbackParts[1] != commandKey.value()) return null
        return callbackParts.subList(2, callbackParts.size)
    }
    fun checkCallbackCommand(commandKey: Key, argCount: Int): List<String>?
        = checkCallbackCommand(commandKey, argCount..argCount)
}
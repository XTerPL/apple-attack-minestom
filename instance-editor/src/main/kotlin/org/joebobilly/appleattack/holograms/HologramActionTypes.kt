package org.joebobilly.appleattack.holograms

import net.thenextlvl.hologram.action.ActionType
import net.thenextlvl.hologram.action.ActionTypeRegistry
import net.thenextlvl.hologram.action.ActionTypes
import net.thenextlvl.hologram.action.ClickAction
import net.thenextlvl.hologram.action.ClickType
import org.bukkit.Bukkit
import org.joebobilly.appleattack.utils.EnumUtils
import java.util.EnumSet

@Suppress("UnstableApiUsage") // why is ActionTypes internal???
object HologramActionTypes {
    private val clickActionFactory = ClickAction.factory()
    private val actionTypes = ActionTypes.types()!!
    private val callbackActionType = ActionType.create("apple_attack-callback", String::class.java) {
        line, player, string ->
        Bukkit.getPluginManager().callEvent(HologramCallbackEvent(line, player, string))
    }!!

    init {
        ActionTypeRegistry.registry().register(callbackActionType)
    }

    fun <T> ActionType<T>.create(clickTypes: EnumSet<ClickType>, input: T)
        = clickActionFactory.create(this, clickTypes, input)!!
    fun <T> ActionType<T>.left(input: T)
        = this.create(EnumUtils.setOf(ClickType.LEFT), input)
    fun <T> ActionType<T>.right(input: T)
        = this.create(EnumUtils.setOf(ClickType.RIGHT), input)
    fun <T> ActionType<T>.shiftLeft(input: T)
        = this.create(EnumUtils.setOf(ClickType.SHIFT_LEFT), input)
    fun <T> ActionType<T>.shiftRight(input: T)
        = this.create(EnumUtils.setOf(ClickType.SHIFT_RIGHT), input)

    fun sendActionbar() = actionTypes.sendActionbar()!!
    fun sendMessage() = actionTypes.sendMessage()!!
    fun sendTitle() = actionTypes.sendTitle()!!
    fun runCommand() = actionTypes.runCommand()!!
    fun runConsoleCommand() = actionTypes.runConsoleCommand()!!
    fun playSound() = actionTypes.playSound()!!
    fun setPage() = actionTypes.setPage()!!
    fun cyclePage() = actionTypes.cyclePage()!!
    fun teleport() = actionTypes.teleport()!!
    fun connect() = actionTypes.connect()!!
    fun transfer() = actionTypes.transfer()!!
    fun callback() = callbackActionType
}
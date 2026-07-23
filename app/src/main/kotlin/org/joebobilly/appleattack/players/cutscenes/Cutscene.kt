package org.joebobilly.appleattack.players.cutscenes

import net.kyori.adventure.text.Component
import org.joebobilly.appleattack.interfaces.UserInterface
import org.joebobilly.appleattack.players.AAPlayer
import java.time.Duration
import java.time.temporal.TemporalUnit

class Cutscene {
    enum class State {
        AWAITING, ONGOING, FINISHED
    }
    enum class Directive {
        STEP, WAIT, STOP
    }

    private var state = State.AWAITING
    private var player: AAPlayer? = null
    internal var currentSpeaker: Speaker = Speaker.GENERIC_SPEAKER
    private val commands = mutableListOf<CutsceneCommand>()

    fun addCommand(command: CutsceneCommand): Cutscene {
        check(state != State.FINISHED) { "The cutscene already finished!" }
        commands.add(command)
        return this
    }

    fun wait(duration: Duration): Cutscene = addCommand(CutsceneCommand.Wait(duration))
    fun wait(amount: Long, temporalUnit: TemporalUnit) = addCommand(CutsceneCommand.Wait(amount, temporalUnit))
    fun waitSeconds(amount: Long) = addCommand(CutsceneCommand.Wait.ofSeconds(amount))
    fun waitMillis(amount: Long) = addCommand(CutsceneCommand.Wait.ofMillis(amount))
    fun waitTicks(amount: Long) = addCommand(CutsceneCommand.Wait.ofTicks(amount))
    fun speak(text: Component, emotion: Speaker.Emotion = Speaker.Emotion.NORMAL, wait: CutsceneCommand.Wait? = null)
        = addCommand(CutsceneCommand.Speak(text, emotion, wait))
    fun speak(text: String, emotion: Speaker.Emotion = Speaker.Emotion.NORMAL, wait: CutsceneCommand.Wait? = null)
        = addCommand(CutsceneCommand.Speak(Component.text(text), emotion, wait))
    fun setSpeaker(speaker: Speaker) = addCommand(CutsceneCommand.SetSpeaker(speaker))
    fun openInterface(interfaceSupplier: () -> UserInterface) = addCommand(CutsceneCommand.OpenInterface(interfaceSupplier))

    fun processCommand() {
        check(state == State.ONGOING) { "The cutscene is not ongoing!" }
        if(!player!!.isOnline) finish()
        while(commands.isNotEmpty()) {
            val command = commands.removeFirst()
            val directive = command.execute(this)
            when(directive) {
                Directive.STEP -> continue
                Directive.WAIT -> return
                Directive.STOP -> break
            }
        }
        finish()
    }

    fun getPlayer(): AAPlayer {
        check(state == State.ONGOING) { "The cutscene is not ongoing!" }
        return player!!
    }

    fun start(player: AAPlayer): Boolean {
        check(state == State.AWAITING) { "The cutscene has already started!" }
        if(player.currentCutscene != null) return false
        state = State.ONGOING
        this.player = player
        processCommand()
        return true
    }

    private fun finish() {
        check(state == State.ONGOING) { "The cutscene is not ongoing!" }
        state = State.FINISHED
        player!!.currentCutscene = null
        player = null
        commands.clear()
    }
}
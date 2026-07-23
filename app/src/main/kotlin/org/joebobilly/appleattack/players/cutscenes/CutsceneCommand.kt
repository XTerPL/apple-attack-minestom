package org.joebobilly.appleattack.players.cutscenes

import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.timer.TaskSchedule
import net.minestom.server.utils.time.TimeUnit
import org.joebobilly.appleattack.interfaces.UserInterface
import java.time.Duration
import java.time.temporal.TemporalUnit

interface CutsceneCommand {
    fun execute(cutscene: Cutscene): Cutscene.Directive

    class Wait(val duration: Duration) : CutsceneCommand {
        companion object {
            fun ofSeconds(amount: Long) = Wait(Duration.ofSeconds(amount))
            fun ofMillis(amount: Long) = Wait(Duration.ofMillis(amount))
            fun ofTicks(amount: Long) = Wait(amount, TimeUnit.SERVER_TICK)
        }

        constructor(amount: Long, unit: TemporalUnit) : this(Duration.of(amount, unit))
        override fun execute(cutscene: Cutscene): Cutscene.Directive {
            MinecraftServer.getSchedulerManager().scheduleTask({
                cutscene.processCommand()
                TaskSchedule.stop()
            }, TaskSchedule.duration(duration))
            return Cutscene.Directive.WAIT
        }
    }
    class Speak(val text: Component,
                val emotion: Speaker.Emotion = Speaker.Emotion.NORMAL,
                val wait: Wait? = null
    ) : CutsceneCommand {
        override fun execute(cutscene: Cutscene): Cutscene.Directive {
            cutscene.currentSpeaker.talk(cutscene.getPlayer(), text, emotion)
            return wait?.execute(cutscene) ?: Cutscene.Directive.STEP
        }
    }
    class SetSpeaker(val newSpeaker: Speaker) : CutsceneCommand {
        override fun execute(cutscene: Cutscene): Cutscene.Directive {
            cutscene.currentSpeaker = newSpeaker
            return Cutscene.Directive.STEP
        }
    }
    class OpenInterface(val interfaceSupplier: () -> UserInterface) : CutsceneCommand {
        override fun execute(cutscene: Cutscene): Cutscene.Directive {
            cutscene.getPlayer().openInventory(interfaceSupplier().inventory)
            return Cutscene.Directive.STEP
        }
    }
}
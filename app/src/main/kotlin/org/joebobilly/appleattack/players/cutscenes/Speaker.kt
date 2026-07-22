package org.joebobilly.appleattack.players.cutscenes

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.utils.Sounds

class Speaker private constructor(
    private val speakerOptions: SpeakerOptions, private val emotionOptions: Map<Emotion, SpeakerOptions>
) {
    enum class Emotion {
        NORMAL
    }

    companion object {
        val GENERIC_SPEAKER = Builder(SpeakerOptions()).build()
    }

    class Builder(private val speakerOptions: SpeakerOptions) {
        private val emotionOptions = mutableMapOf<Emotion, SpeakerOptions>()

        fun addEmotion(emotion: Emotion, options: SpeakerOptions): Builder {
            emotionOptions[emotion] = options
            return this
        }

        fun build(): Speaker {
            return Speaker(speakerOptions, emotionOptions.toMap())
        }
    }

    data class SpeakerOptions(
        val title: Component? = null,
        val primaryTextStyle: Style? = null,
        val sound: Sound? = null
    ) {
        fun withTitle(title: Component): SpeakerOptions {
            return SpeakerOptions(title = title, primaryTextStyle = primaryTextStyle, sound = sound)
        }
        fun withPrimaryTextStyle(primaryTextStyle: Style): SpeakerOptions {
            return SpeakerOptions(title = title, primaryTextStyle = primaryTextStyle, sound = sound)
        }
        fun withSound(sound: Sound): SpeakerOptions {
            return SpeakerOptions(title = title, primaryTextStyle = primaryTextStyle, sound = sound)
        }
        fun overlay(overlay: SpeakerOptions?): SpeakerOptions {
            if(overlay == null) return this
            return SpeakerOptions(
                overlay.title ?: title,
                overlay.primaryTextStyle ?: primaryTextStyle,
                overlay.sound ?: sound
            )
        }
    }

    fun getOptionsForEmotion(emotion: Emotion): SpeakerOptions {
        return speakerOptions.overlay(emotionOptions[emotion])
    }

    fun talk(player: AAPlayer, text: Component, emotion: Emotion = Emotion.NORMAL) {
        val options = getOptionsForEmotion(emotion)

        val primaryTextStyle = options.primaryTextStyle ?: Style.style(NamedTextColor.GOLD)
        val sound = options.sound ?: Sounds.GENERIC_TALK

        val chatMessage = Component.empty().append(options.title?.appendSpace() ?: Component.empty())
            .append(Component.text("» ", NamedTextColor.DARK_GRAY))
            .append(Component.empty().style(primaryTextStyle).append(text))

        player.sendMessage(chatMessage)
        player.playSound(sound)
    }
}
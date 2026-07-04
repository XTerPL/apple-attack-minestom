package org.joebobilly.appleattack.blockhandlers

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.tag.Tag
import org.joebobilly.appleattack.utils.SignText

abstract class SignBlockHandler : BlockHandler {
    companion object {
        val isWaxed: Tag<Boolean?> = Tag.Boolean("is_waxed")
        val frontText = SignText.tag("front_text")
        val backText = SignText.tag("back_text")
    }

    class Regular : SignBlockHandler() {
        companion object {
            val staticKey: Key = Key.key("minecraft", "sign")
        }

        override fun getKey(): Key {
            return staticKey
        }
    }

    class Hanging : SignBlockHandler() {
        companion object {
            val staticKey: Key = Key.key("minecraft", "hanging_sign")
        }

        override fun getKey(): Key {
            return staticKey
        }
    }

    override fun getBlockEntityTags(): Collection<Tag<*>?> {
        return listOf(isWaxed, frontText, backText)
    }
}
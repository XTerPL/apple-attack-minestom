package org.joebobilly.appleattack.blockhandlers

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.tag.Tag
import org.joebobilly.appleattack.utils.SignText

sealed class SignBlockHandler : BlockHandler {
    companion object {
        val isWaxed: Tag<Boolean> = Tag.Boolean("is_waxed")
        val frontText = SignText.tag("front_text")
        val backText = SignText.tag("back_text")
    }

    object Regular : SignBlockHandler() {
        override fun getKey(): Key {
            return Key.key("minecraft", "sign")
        }
    }

    object Hanging : SignBlockHandler() {
        override fun getKey(): Key {
            return Key.key("minecraft", "hanging_sign")
        }
    }

    override fun getBlockEntityTags(): Collection<Tag<*>> {
        return listOf(isWaxed, frontText, backText)
    }
}
package org.joebobilly.appleattack.blockhandlers

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.tag.Tag
import org.joebobilly.appleattack.utils.SignText
import org.joebobilly.appleattack.utils.TagUtils

object SkullBlockHandler : BlockHandler {
    val customName: Tag<Component> = Tag.Component("custom_name")
    val noteBlockSound = TagUtils.keyTag("note_block_sound")
    val profile = TagUtils.resolvableProfileTag("profile")

    override fun getKey(): Key {
        return Key.key("minecraft", "skull")
    }

    override fun getBlockEntityTags(): Collection<Tag<*>> {
        return listOf(customName, noteBlockSound, profile)
    }
}
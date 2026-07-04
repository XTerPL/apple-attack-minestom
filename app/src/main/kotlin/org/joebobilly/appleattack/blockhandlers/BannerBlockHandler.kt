package org.joebobilly.appleattack.blockhandlers

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.tag.Tag
import org.joebobilly.appleattack.utils.TagUtils

class BannerBlockHandler : BlockHandler {
    companion object {
        val staticKey: Key = Key.key("minecraft", "banner")

        val customName: Tag<Component?> = Tag.Component("CustomName")
        val patterns = TagUtils.bannerPatternsTag("patterns")
    }

    override fun getKey(): Key {
        return staticKey
    }

    override fun getBlockEntityTags(): Collection<Tag<*>?> {
        return listOf(customName, patterns)
    }
}
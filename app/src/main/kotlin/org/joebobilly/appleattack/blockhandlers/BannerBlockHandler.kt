package org.joebobilly.appleattack.blockhandlers

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.tag.Tag
import org.joebobilly.appleattack.utils.TagUtils

object BannerBlockHandler : BlockHandler {
    val customName: Tag<Component?> = Tag.Component("CustomName")
    val patterns = TagUtils.bannerPatternsTag("patterns")

    override fun getKey(): Key {
        return Key.key("minecraft", "banner")
    }

    override fun getBlockEntityTags(): Collection<Tag<*>> {
        return listOf(customName, patterns)
    }
}
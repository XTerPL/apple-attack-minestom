package org.joebobilly.appleattack.utils

import net.kyori.adventure.text.Component
import net.minestom.server.adventure.MinestomAdventure
import net.minestom.server.color.DyeColor
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagSerializer
import net.minestom.server.tag.TagWritable

class SignText(val hasGlowingText: Boolean, val color: DyeColor, val messages: List<Component>) {
    companion object {
        fun tag(key: String): Tag<SignText?> {
            return Tag.Structure<SignText>(key, Serializer())
        }
    }

    class Serializer : TagSerializer<SignText> {
        companion object {
            private val hasGlowingText = Tag.Boolean("has_glowing_text").defaultValue(false)
            private val color = TagUtils.enumTag<DyeColor>("color").defaultValue(DyeColor.BLACK)
            private val messages = TagUtils.componentListTag("messages").defaultValue(listOf())
        }

        override fun read(reader: TagReadable?): SignText? {
            if(reader == null) return null
            val hasGlowingText = reader.getTag(hasGlowingText)
            val color = reader.getTag(color)
            val messages = reader.getTag(messages)
            return SignText(hasGlowingText ?: false, color ?: DyeColor.BLACK, messages ?: listOf())
        }

        override fun write(writer: TagWritable?, value: SignText?) {
            if(writer == null || value == null) return
            writer.setTag(hasGlowingText, value.hasGlowingText)
            writer.setTag(color, value.color)
            writer.setTag(messages, value.messages)
        }
    }
}
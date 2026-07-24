package org.joebobilly.appleattack.utils

import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagWritable

object UnitTagSerializer : TagCopySerializer<Unit> {
    override fun read(reader: TagReadable) {}

    override fun write(writer: TagWritable, value: Unit) {}

    override fun copy(value: Unit) {}
}
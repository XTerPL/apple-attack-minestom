package org.joebobilly.appleattack.utils

import net.minestom.server.tag.TagSerializer

// provides both Tag serialization as well as copy abilities
interface TagCopySerializer<T> : TagSerializer<T> {
    fun copy(value: T): T
}
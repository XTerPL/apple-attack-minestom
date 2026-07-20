package org.joebobilly.appleattack.items.icons

import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.utils.EnumUtils
import java.util.EnumSet

open class ItemIcon(val model: String, val flags: EnumSet<Flag> = EnumUtils.emptySet()) {
    enum class Flag {

    }

    constructor(material: Material, flags: EnumSet<Flag> = EnumUtils.emptySet())
            : this(material.key().asString(), flags)
    open fun apply(builder: ItemStack.Builder) {
        builder.itemModel(model)
    }
}
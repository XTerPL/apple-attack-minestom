package org.joebobilly.appleattack.items

import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.utils.UnitTagSerializer

abstract class BasicAAItem(id: String, maxCount: Int = 64, backingMaterial: Material = Material.STRUCTURE_BLOCK)
    : AAItem<Unit>(id, UnitTagSerializer, maxCount, backingMaterial) {
    fun create(count: Int = 1): ItemStack {
        return create(count, Unit)
    }
}
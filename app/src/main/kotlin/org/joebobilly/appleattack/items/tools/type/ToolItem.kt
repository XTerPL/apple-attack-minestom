package org.joebobilly.appleattack.items.tools.type

import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.joebobilly.appleattack.items.AAItem
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.AARarity
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ForgeMaterial
import org.joebobilly.appleattack.items.tools.ForgedToolMeta
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.items.icons.ItemIcon
import org.joebobilly.appleattack.utils.TagCopySerializer

sealed class ToolItem<METATYPE : ToolMeta>(id: String, val toolType: ToolType, metaSerializer: TagCopySerializer<METATYPE>)
    : AAItem<METATYPE>(id, metaSerializer, 1) {
    init {
        ItemProperty.TOOL_DATA.set {
            val builder = ToolDefinition.Builder(toolType)
            defineTool(it, builder)
            builder.build().toProvider(it)
        }
    }

    // checks for modifier slots and traits over max level
    fun safelyAddUpgradeToMeta(meta: METATYPE, upgrade: AAItemMetaPair<*>): Boolean {
        if(!meta.addUpgrade(upgrade)) return false
        if(getProperty(ItemProperty.TOOL_DATA, meta).isOverloaded()) {
            meta.removeLatestUpgrade()
            return false
        }
        return true
    }

    // adds the same upgrade until it can't or gets limited
    fun safelyAddUpgradesToMeta(meta: METATYPE, upgrade: ItemStack, limit: Int?): Int {
        val itemMetaPair = AAItemManager.getItemMetaPair(upgrade) ?: return 0
        if(!itemMetaPair.hasProperty(ItemProperty.FORGE_UPGRADE_DATA)) return 0
        val limit = limit?.coerceIn(0, upgrade.amount()) ?: upgrade.amount()
        if(limit <= 0) return 0
        for(i in 0..<limit) {
            if(!safelyAddUpgradeToMeta(meta, itemMetaPair)) {
                return i
            }
        }
        return limit
    }

    protected abstract fun defineTool(meta: METATYPE, builder: ToolDefinition.Builder)

    companion object {
        fun <RECIPE : ForgedToolMeta.Recipe> ToolItem<ForgedToolMeta<RECIPE>>.initForged() {
            setPropertyProvider(ItemProperty.NAME) {
                ForgeMaterial.getName(it.getForgeMaterials(), this.toolType)
            }
            setPropertyProvider(ItemProperty.ICON) {
                it.getCoreMaterial().definition.icon ?: ItemIcon(Material.BARRIER)
            }
            setPropertyProvider(ItemProperty.RARITY) { AARarity.FORGED }
        }
    }
}
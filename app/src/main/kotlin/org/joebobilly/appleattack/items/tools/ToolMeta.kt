package org.joebobilly.appleattack.items.tools

import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagWritable
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.utils.TagCopySerializer

open class ToolMeta {
    private val upgradeItems = mutableListOf<AAItemMetaPair<*>>()
    val upgrades: List<ForgeUpgradeData>
        get() = upgradeItems.map { it.getProperty(ItemProperty.FORGE_UPGRADE_DATA) }

    // adds upgrade regardless of if it will overload the tool
    fun addUpgrade(itemMetaPair: AAItemMetaPair<*>): Boolean {
        if(!itemMetaPair.hasProperty(ItemProperty.FORGE_UPGRADE_DATA)) return false
        upgradeItems.add(itemMetaPair)
        return true
    }

    // removes last upgrade applied
    fun removeLatestUpgrade(): AAItemMetaPair<*>? {
        return upgradeItems.removeLastOrNull()
    }

    fun clearUpgrades() {
        upgradeItems.clear()
    }

    fun <RECIPE : ForgedToolMeta.Recipe> withRecipe(recipe: RECIPE): ForgedToolMeta<RECIPE> {
        val meta = ForgedToolMeta(recipe)
        upgradeItems.forEach {
            meta.addUpgrade(it)
        }
        return meta
    }

    object Serializer : TagCopySerializer<ToolMeta> {
        private val upgradeItems = AAItemMetaPair.tag("upgrades").list().defaultValue(emptyList())

        override fun read(reader: TagReadable): ToolMeta {
            val upgradeItems = reader.getTag(upgradeItems).filter { it?.hasProperty(ItemProperty.FORGE_UPGRADE_DATA) == true }
            return ToolMeta().apply {
                this.upgradeItems.addAll(upgradeItems)
            }
        }

        override fun write(writer: TagWritable, value: ToolMeta) {
            writer.setTag(upgradeItems, value.upgradeItems)
        }

        override fun copy(value: ToolMeta): ToolMeta {
            val meta = ToolMeta()
            value.upgradeItems.forEach {
                meta.addUpgrade(it)
            }
            return meta
        }
    }
}
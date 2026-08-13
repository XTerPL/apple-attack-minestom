package org.joebobilly.appleattack.items.tools

import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTCopySerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.list
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry

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

    object Serializer : NBTCopySerializer<ToolMeta>(ToolMeta::class) {
        private val upgradeItems = AAItemMetaPair.Serializer.list().toEntry("upgrades")

        override fun read(context: DeserializationContext): ToolMeta {
            val upgradeItems = context.read(upgradeItems) { emptyList() }
                .filter { it.hasProperty(ItemProperty.FORGE_UPGRADE_DATA) }
            return ToolMeta().apply {
                this.upgradeItems.addAll(upgradeItems)
            }
        }

        override fun write(context: SerializationContext, value: ToolMeta) {
            context.write(upgradeItems, value.upgradeItems)
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
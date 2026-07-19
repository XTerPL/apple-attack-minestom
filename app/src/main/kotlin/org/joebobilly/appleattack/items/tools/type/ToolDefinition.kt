package org.joebobilly.appleattack.items.tools.type

import org.joebobilly.appleattack.items.tools.ForgeMaterial
import org.joebobilly.appleattack.items.tools.ToolData
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.items.tools.ToolStat
import org.joebobilly.appleattack.items.tools.traits.Trait

class ToolDefinition private constructor(
    internal val toolType: ToolType,
    internal val stats: Map<ToolStat<*>, ToolStat.StatEntry<*>>,
    internal val baseTraits: Map<Trait, Int>
) {
    class Builder internal constructor(val toolType: ToolType) {
        private var stats = mutableMapOf<ToolStat<*>, ToolStat.StatEntryList<*>>()
        private var baseTraits = mutableMapOf<Trait, Int>()

        init {
            toolType.providedStats.forEach {
                stats[it] = ToolStat.StatEntryList(it)
            }
        }

        fun addMaterial(material: ForgeMaterial): Builder {
            material.traitsAdded.forEach {
                addTrait(it.key, it.value)
            }
            material.getStats().forEach {
                addStat(it)
            }
            return this
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> addStat(stat: ToolStat<T>, value: T): Builder {
            if(stats.containsKey(stat)) {
                (stats[stat] as ToolStat.StatEntryList<T>).addStat(value)
            }
            return this
        }

        fun <T> addStat(entry: ToolStat.StatEntry<T>): Builder {
            return addStat(entry.stat, entry.value)
        }

        fun addTrait(trait: Trait, level: Int): Builder {
            val newLevel = (baseTraits[trait] ?: 0) + level
            if(newLevel == 0) {
                baseTraits.remove(trait)
            }
            else {
                baseTraits[trait] = newLevel
            }
            return this
        }

        fun build(): ToolDefinition {
            val finalStats = mutableMapOf<ToolStat<*>, ToolStat.StatEntry<*>>()
            stats.forEach {
                finalStats[it.key] = it.value.getFinalStat()
            }
            baseTraits.forEach {
                baseTraits[it.key] = it.value.coerceIn(0, it.key.maxLevel)
            }
            return ToolDefinition(toolType, finalStats.toMap(), baseTraits.toMap())
        }
    }

    fun toProvider(toolMeta: ToolMeta): ToolData.Provider {
        return ToolData.Provider(this, toolMeta)
    }
}
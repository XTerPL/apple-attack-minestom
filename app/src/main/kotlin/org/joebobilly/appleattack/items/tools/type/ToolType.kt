package org.joebobilly.appleattack.items.tools.type

import org.joebobilly.appleattack.items.tools.ToolStat

enum class ToolType(val displayName: String, vararg providedStats: ToolStat<*>) {
    SWORD("Sword", ToolStat.ATTACK);

    val providedStats: List<ToolStat<*>>
    init {
        val list = providedStats.toMutableList()
        list.add(ToolStat.MODIFIER_SLOTS)
        this.providedStats = list.toList()
    }
}
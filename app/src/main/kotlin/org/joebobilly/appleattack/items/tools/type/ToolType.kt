package org.joebobilly.appleattack.items.tools.type

import org.joebobilly.appleattack.items.tools.ToolStat

enum class ToolType(vararg providedStats: ToolStat<*>) {
    SWORD(ToolStat.ATTACK);

    val providedStats: List<ToolStat<*>>
    init {
        val list = providedStats.toMutableList()
        list.add(ToolStat.MODIFIER_SLOTS)
        this.providedStats = list.toList()
    }
}
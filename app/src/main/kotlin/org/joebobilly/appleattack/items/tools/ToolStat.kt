package org.joebobilly.appleattack.items.tools

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import kotlin.math.floor

class ToolStat<T> private constructor(
    val displayName: String,
    val tooltipOrder: TooltipOrder,
    private val icon: String,
    private val styleProvider: (T, ToolData?, ToolMeta?) -> Style,
    private val combination: (List<T>) -> T,
    private val displayModifier: (T, ToolData?, ToolMeta?) -> String = { a, _, _ -> a.toString() }
) {
    companion object {
        val MODIFIER_SLOTS = ToolStat(
            "modifier slots", TooltipOrder.MODIFIER_SLOTS, "✂", {
                value, toolData, _ ->
                if(toolData?.isOverloaded() == true) {
                    Style.style(NamedTextColor.DARK_RED).decorate(TextDecoration.STRIKETHROUGH)
                } else {
                    Style.style(NamedTextColor.AQUA)
                }
            },
            sumStats(0, { a, b -> a + b }), {
                value, toolData, _ -> (value - (toolData?.usedModifiers ?: 0)).toString()
            }
        )
        val ATTACK = ToolStat(
            "attack", TooltipOrder.MAIN, "\uD83D\uDDE1", Style.style(NamedTextColor.RED),
            sumStats(
                0.0, { a, b -> a + b }, { a, _ -> a.coerceAtLeast(1.0) }
            ),
            ::cleanUpDouble
        )

        private fun <T : Number> sumStats(
            start: T, sumFunction: (T, T) -> T, endFunction: (T, Int) -> T = { a, _ -> a }
        ): (List<T>) -> T = {
            var sum = start
            for(stat in it) {
                sum = sumFunction(sum, stat)
            }
            endFunction(sum, it.count())
        }

        private fun cleanUpDouble(value: Double, toolData: ToolData?, toolMeta: ToolMeta?): String {
            if(floor(value) == value) return value.toInt().toString()
            return value.toString()
        }

        val comparator: Comparator<StatEntry<*>> =
            Comparator.comparing<StatEntry<*>, _> { it.stat.tooltipOrder }.thenComparing { it.stat.displayName }

        fun getStatDisplays(stats: Collection<StatEntry<*>>, toolData: ToolData, toolMeta: ToolMeta): List<Component> {
            return stats.sortedWith(comparator).map { it.getStatDisplay(toolData, toolMeta) }
        }
        fun getStatDisplays(stats: Collection<StatEntry<*>>): List<Component> {
            return stats.sortedWith(comparator).map { it.getStatDisplay() }
        }
        fun combineStatDisplays(statDisplays: List<Component>): Component {
            return Component.join(JoinConfiguration.builder()
                .separator(Component.text(" | ", NamedTextColor.DARK_GRAY)), statDisplays)
        }
    }

    enum class TooltipOrder {
        MAIN,
        MODIFIER_SLOTS,
        MISC
    }

    private constructor(
        displayName: String,
        tooltipOrder: TooltipOrder,
        icon: String,
        style: Style,
        combination: (List<T>) -> T,
        displayModifier: (T, ToolData?, ToolMeta?) -> String = { a, _, _ -> a.toString() }
    ) : this(displayName, tooltipOrder, icon, { _, _, _ -> style }, combination, displayModifier)

    data class StatEntry<T>(val stat: ToolStat<T>, val value: T) {
        fun getStatDisplay(toolData: ToolData, toolMeta: ToolMeta): Component {
            return stat.getStatDisplay(value, toolData, toolMeta)
        }
        fun getStatDisplay(): Component {
            return stat.getStatDisplay(value)
        }
    }
    class StatEntryList<T>(val stat: ToolStat<T>) {
        val values = mutableListOf<T>()
        fun addStat(value: T) {
            values.add(value)
        }
        fun getFinalStat(): StatEntry<T> {
            return StatEntry(stat, stat.combination(values))
        }
    }

    fun getStatDisplay(value: T, toolData: ToolData, toolMeta: ToolMeta): Component {
        val label = icon + displayModifier(value, toolData, toolMeta)
        return Component.text(label, styleProvider(value, toolData, toolMeta))
    }

    fun getStatDisplay(value: T): Component {
        val label = icon + displayModifier(value, null, null)
        return Component.text(label, styleProvider(value, null, null))
    }
}
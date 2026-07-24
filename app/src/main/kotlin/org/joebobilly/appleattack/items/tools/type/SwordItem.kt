package org.joebobilly.appleattack.items.tools.type

import net.kyori.adventure.text.Component
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagWritable
import org.joebobilly.appleattack.damage.AttackInfo
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ForgeMaterial
import org.joebobilly.appleattack.items.tools.ForgedToolMeta
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.items.tools.ToolStat
import org.joebobilly.appleattack.utils.TagCopySerializer
import org.joebobilly.appleattack.utils.TagUtils.getTagOrThrow

sealed class SwordItem<METATYPE : ToolMeta>(id: String, metaSerializer: TagCopySerializer<METATYPE>)
    : ToolItem<METATYPE>(id, ToolType.SWORD, metaSerializer) {
    abstract class Defined(id: String) : SwordItem<ToolMeta>(id, ToolMeta.Serializer)
    object Forged : SwordItem<ForgedToolMeta<Recipe>>("forged_sword",
        ForgedToolMeta.Serializer(Recipe.Serializer)
    ) {
        init {
            initForged()
        }

        override fun defineTool(
            meta: ForgedToolMeta<Recipe>,
            builder: ToolDefinition.Builder
        ) {
            meta.getForgeMaterials().forEach {
                builder.addMaterial(it)
            }
        }

        override fun defaultName() = Component.text("Forged Sword")
    }

    data class Recipe(
        val handle: AAItemMetaPair<*>,
        val bladeDown : AAItemMetaPair<*>,
        val bladeUp : AAItemMetaPair<*>
    ) : ForgedToolMeta.Recipe(listOf(handle, bladeDown, bladeUp)) {
        init {
            requireMaterialType<ForgeMaterial.Handle>(handle, "handle")
            requireMaterialType<ForgeMaterial.Attack>(bladeDown, "blade_down")
            requireMaterialType<ForgeMaterial.Attack>(bladeUp, "blade_up")
        }

        override fun getCoreMaterial(): AAItemMetaPair<*> {
            return bladeDown
        }

        object Serializer : TagCopySerializer<Recipe> {
            val handle = AAItemMetaPair.tag("handle")
            val bladeDown = AAItemMetaPair.tag("blade_down")
            val bladeUp = AAItemMetaPair.tag("blade_up")

            override fun read(reader: TagReadable): Recipe {
                return Recipe(
                    reader.getTagOrThrow(handle),
                    reader.getTagOrThrow(bladeDown),
                    reader.getTagOrThrow(bladeUp)
                )
            }

            override fun write(writer: TagWritable, value: Recipe) {
                writer.setTag(handle, value.handle)
                writer.setTag(bladeDown, value.bladeDown)
                writer.setTag(bladeUp, value.bladeUp)
            }

            override fun copy(value: Recipe): Recipe {
                return value.copy()
            }
        }
    }

    init {
        ItemProperty.MELEE_ATTACK.set {
            val toolData = getProperty(ItemProperty.TOOL_DATA, it)
            AttackInfo.melee(toolData.getStat(ToolStat.ATTACK))
        }
    }
}
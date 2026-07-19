package org.joebobilly.appleattack.items.tools.type

import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagSerializer
import net.minestom.server.tag.TagWritable
import org.joebobilly.appleattack.damage.AttackInfo
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ForgeMaterial
import org.joebobilly.appleattack.items.tools.ForgedToolMeta
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.items.tools.ToolStat
import org.joebobilly.appleattack.utils.NBTReadError
import org.joebobilly.appleattack.utils.TagUtils.getTagSourced

sealed class SwordItem<METATYPE : ToolMeta>(id: String, metaSerializer: TagSerializer<METATYPE>)
    : ToolItem<METATYPE>(id, ToolType.SWORD, metaSerializer) {
    abstract class Defined(id: String) : SwordItem<ToolMeta>(id, ToolMeta.Serializer)
    object Forged : SwordItem<ForgedToolMeta<Recipe>>("forged_sword",
        ForgedToolMeta.Serializer(Recipe.Serializer)
    ) {
        override fun defineTool(
            meta: ForgedToolMeta<Recipe>,
            builder: ToolDefinition.Builder
        ) {
            meta.getForgeMaterials().forEach {
                builder.addMaterial(it)
            }
        }
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

        object Serializer : TagSerializer<Recipe> {
            val handle = AAItemMetaPair.tag("handle")
            val bladeDown = AAItemMetaPair.tag("blade_down")
            val bladeUp = AAItemMetaPair.tag("blade_up")

            override fun read(reader: TagReadable): Recipe {
                return Recipe(
                    reader.getTagSourced(handle) ?: throw NBTReadError("handle", "not found"),
                    reader.getTagSourced(bladeDown) ?: throw NBTReadError("blade_down", "not found"),
                    reader.getTagSourced(bladeUp) ?: throw NBTReadError("blade_up", "not found")
                )
            }

            override fun write(writer: TagWritable, value: Recipe) {
                writer.setTag(handle, value.handle)
                writer.setTag(bladeDown, value.bladeDown)
                writer.setTag(bladeUp, value.bladeUp)
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
package org.joebobilly.appleattack.items.tools.type

import net.kyori.adventure.text.Component
import org.joebobilly.appleattack.damage.AttackInfo
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.items.tools.ForgeMaterial
import org.joebobilly.appleattack.items.tools.ForgedToolMeta
import org.joebobilly.appleattack.items.tools.ToolMeta
import org.joebobilly.appleattack.items.tools.ToolStat
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTCopySerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry

sealed class SwordItem<METATYPE : ToolMeta>(id: String, metaSerializer: NBTCopySerializer<METATYPE>)
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

        object Serializer : NBTCopySerializer<Recipe> {
            val handle = AAItemMetaPair.Serializer.toEntry("handle")
            val bladeDown = AAItemMetaPair.Serializer.toEntry("blade_down")
            val bladeUp = AAItemMetaPair.Serializer.toEntry("blade_up")

            override val klass = Recipe::class

            override fun read(context: DeserializationContext): Recipe {
                return Recipe(
                    context.read(handle),
                    context.read(bladeDown),
                    context.read(bladeUp)
                )
            }

            override fun write(context: SerializationContext, value: Recipe) {
                context.write(handle, value.handle)
                context.write(bladeDown, value.bladeDown)
                context.write(bladeUp, value.bladeUp)
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
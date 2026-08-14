package org.joebobilly.appleattack.items.tools

import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTCopySerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import kotlin.reflect.KClass

class ForgedToolMeta<RECIPE : ForgedToolMeta.Recipe>(val recipe: RECIPE) : ToolMeta() {
    abstract class Recipe(internal val forgeMaterials : List<AAItemMetaPair<*>>) {
        init {
            require(forgeMaterials.all {
                it.hasProperty(ItemProperty.FORGE_MATERIAL)
            }) {
                "A non-forge material was found in a forge recipe!"
            }
        }

        abstract fun getCoreMaterial(): AAItemMetaPair<*>

        companion object {
            inline fun <reified T : ForgeMaterial> requireMaterialType(itemMetaPair: AAItemMetaPair<*>, pieceName: String) {
                val materialName = T::class.simpleName ?: "Unknown"
                require(itemMetaPair.withProperty(ItemProperty.FORGE_MATERIAL) { it is T } ?: false) {
                    "Piece $pieceName was not of required material type $materialName"
                }
            }
        }
    }

    fun getForgeMaterials() : List<ForgeMaterial> {
        return recipe.forgeMaterials.map { it.getProperty(ItemProperty.FORGE_MATERIAL) }
    }

    fun getCoreMaterial(): ForgeMaterial {
        return recipe.getCoreMaterial().getProperty(ItemProperty.FORGE_MATERIAL)
    }

    @Suppress("UNCHECKED_CAST")
    class Serializer<RECIPE : Recipe>(private val recipeSerializer: NBTCopySerializer<RECIPE>)
        : NBTCopySerializer<ForgedToolMeta<RECIPE>> {
        private val recipe = recipeSerializer.toEntry("recipe")

        override val klass = ForgedToolMeta::class as KClass<ForgedToolMeta<RECIPE>>

        override fun read(context: DeserializationContext): ForgedToolMeta<RECIPE> {
            val recipe = context.read(recipe)
            return ToolMeta.Serializer.read(context).withRecipe(recipe)
        }

        override fun write(context: SerializationContext, value: ForgedToolMeta<RECIPE>) {
            context.write(recipe, value.recipe)
            ToolMeta.Serializer.write(context, value)
        }

        override fun copy(value: ForgedToolMeta<RECIPE>): ForgedToolMeta<RECIPE> {
            val meta = ToolMeta.Serializer.copy(value)
            return meta.withRecipe(recipeSerializer.copy(value.recipe))
        }
    }
}
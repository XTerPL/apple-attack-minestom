package org.joebobilly.appleattack.items.tools

import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagSerializer
import net.minestom.server.tag.TagWritable
import org.joebobilly.appleattack.items.AAItemMetaPair
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.utils.TagUtils.getTagOrThrow

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

    class Serializer<RECIPE : Recipe>(recipeSerializer: TagSerializer<RECIPE>) : TagSerializer<ForgedToolMeta<RECIPE>> {
        private val recipe = Tag.Structure("recipe", recipeSerializer)

        override fun read(reader: TagReadable): ForgedToolMeta<RECIPE> {
            val recipe = reader.getTagOrThrow(recipe)
            return ToolMeta.Serializer.read(reader).withRecipe(recipe)
        }

        override fun write(writer: TagWritable, value: ForgedToolMeta<RECIPE>) {
            writer.setTag(recipe, value.recipe)
            ToolMeta.Serializer.write(writer, value)
        }
    }
}
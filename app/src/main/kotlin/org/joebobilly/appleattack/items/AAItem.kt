package org.joebobilly.appleattack.items

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.component.DataComponents
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagHandler
import net.minestom.server.tag.TagSerializer

abstract class AAItem<METATYPE>(val id: String, private val metaSerializer: TagSerializer<METATYPE>, val maxCount: Int = 64, val backingMaterial: Material = Material.STRUCTURE_BLOCK) {
    companion object {
        private val idTag = Tag.String("id").map<AAItem<*>>(AAItemManager::getItem, AAItem<*>::id)
    }

    private val metaTag = Tag.Structure<METATYPE>("meta", metaSerializer)

    init {
        require(maxCount in 1..99) { "maxCount must be between 1 and 99, got $maxCount" }
        require(backingMaterial != Material.AIR) { "backingMaterial must not be 'AIR'" }
        require(!AAItemManager.isFrozen()) {
            "Cannot create item type $id after server startup (did you forget to register this type?)"
        }
    }

    // creation
    fun create(meta: METATYPE): ItemStack {
        return create(1, meta)
    }
    fun create(count: Int, meta: METATYPE): ItemStack {
        require(count in 1..maxCount) { "count must be between 1 and $maxCount" }
        val builder = ItemStack.builder(backingMaterial)
        update(builder, meta)
        return builder.material(backingMaterial)
                      .maxStackSize(maxCount).amount(count)
                      .set(idTag, this).set(metaTag, meta).build()
    }

    // modification
    fun update(itemStack: ItemStack, metaModifier: (METATYPE) -> METATYPE = { meta -> meta }): ItemStack {
        val meta = getMeta(itemStack) ?: return itemStack
        return create(itemStack.amount(), metaModifier(meta))
    }

    // identification
    fun isItem(itemStack: ItemStack): Boolean {
        return itemStack.getTag(idTag) == this
    }
    fun getMeta(itemStack: ItemStack): METATYPE? {
        if(isItem(itemStack)) return itemStack.getTag(metaTag)
        return null
    }

    // serialization
    fun deserializeMeta(nbt: CompoundBinaryTag): METATYPE? {
        return metaSerializer.read(TagHandler.fromCompound(nbt))
    }

    // item definition
    protected abstract fun name(meta: METATYPE): Component
    protected open fun rarity(meta: METATYPE): AARarity {
        return AARarity.COMMON
    }
    protected open fun itemTypeName(meta: METATYPE): String {
        return ""
    }
    protected open fun description(meta: METATYPE): List<String> {
        return listOf()
    }
    protected open fun itemModel(meta: METATYPE): String {
        return backingMaterial.key().asString()
    }
    protected open fun glow(meta: METATYPE): Boolean {
        return false
    }

    private fun lore(meta: METATYPE): List<Component> {
        val lore = mutableListOf<Component>()

        val description = description(meta)
        if(description.isNotEmpty()) {
            lore.addAll(description.map {
                line ->
                Component.empty()
                    .append(Component.text(" » ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(line, NamedTextColor.GRAY))
            })
        }

        lore.add(rarity(meta).wrapType(itemTypeName(meta)))

        return lore.map { line -> line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE) }
    }
    protected open fun update(builder: ItemStack.Builder, meta: METATYPE) {
        builder.set(DataComponents.ITEM_NAME, name(meta)).lore(lore(meta)).itemModel(itemModel(meta)).hideExtraTooltip()
               .glowing(glow(meta))
    }
}
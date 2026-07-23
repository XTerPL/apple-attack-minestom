package org.joebobilly.appleattack.items

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.text.Component
import net.minestom.server.component.DataComponents
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagHandler
import net.minestom.server.tag.TagSerializer
import org.joebobilly.appleattack.utils.InventoryUtils

abstract class AAItem<METATYPE>(
    val id: String,
    private val metaSerializer: TagSerializer<METATYPE>,
    val maxCount: Int = 64, val backingMaterial: Material = Material.STRUCTURE_BLOCK
) {
    companion object {
        internal val idTag = Tag.String("id")
        internal val itemTag = idTag.map<AAItem<*>>(AAItemManager::get, AAItem<*>::id)
    }

    internal val metaTag = Tag.Structure("meta", metaSerializer)
    private val properties = ItemProperty.Map<METATYPE>()

    init {
        require(maxCount in 1..99) { "maxCount must be between 1 and 99, got $maxCount" }
        require(backingMaterial != Material.AIR) { "backingMaterial must not be 'AIR'" }
        AAItemManager.throwIfFrozen { "Cannot create item type $id after server startup (did you forget to register this type?)" }
        ItemProperty.NAME.set {
            defaultName()
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
                      .set(itemTag, this).set(metaTag, meta).build()
    }

    // modification
    fun update(itemStack: ItemStack, metaModifier: (METATYPE) -> METATYPE = { meta -> meta }): ItemStack {
        val meta = getMeta(itemStack) ?: return itemStack
        return create(itemStack.amount(), metaModifier(meta))
    }

    // identification
    fun isItem(itemStack: ItemStack): Boolean {
        return itemStack.getTag(itemTag) == this
    }
    fun getMeta(itemStack: ItemStack): METATYPE? {
        if(isItem(itemStack)) return itemStack.getTag(metaTag)
        return null
    }
    fun getMetaPair(itemStack: ItemStack): AAItemMetaPair<METATYPE>? {
        val meta = getMeta(itemStack) ?: return null
        return AAItemMetaPair(this, meta)
    }

    // serialization
    fun deserializeMeta(nbt: CompoundBinaryTag): METATYPE? {
        return metaSerializer.read(TagHandler.fromCompound(nbt))
    }

    // item definition

    fun <T, R> ItemProperty<T, R>.set(provider: (METATYPE) -> R) {
        setPropertyProvider(this, provider)
    }
    fun <T> ItemProperty<T, T>.append(nextProvider: (T, METATYPE) -> T) {
        check(hasProperty(this)) {
            "Cannot append another provider for the property $name " +
                    "when there isn't even a previous provider yet... (use .set instead of .append)"
        }
        val entry = properties.getEntry(this)
        if(entry == null) {
            set {
                nextProvider(default!!.invoke(this@AAItem), it)
            }
        }
        else {
            set {
                nextProvider(entry.provider(it), it)
            }
        }
    }
    internal fun <T, R> setPropertyProvider(property: ItemProperty<T, R>, provider: (METATYPE) -> R) {
        AAItemManager.throwIfFrozen { "Cannot modify a property of item type $id after server startup" }
        properties.setEntry(property, provider)
    }

    fun hasProperty(property: ItemProperty<*, *>): Boolean {
        if(property.default != null) return true
        return properties.getEntry(property) != null
    }
    // throws if property doesn't exist on this item type
    fun <T, R> getProperty(property: ItemProperty<T, R>, meta: METATYPE): T {
        val entry = properties.getEntry(property) ?: return property.getDefaultOrThrow(this)
        return property.postProcess(entry.provider(meta))
    }
    fun <T, R, U> withProperty(property: ItemProperty<T, R>, meta: METATYPE, consumer: (T) -> U): U? {
        if(hasProperty(property)) {
            return consumer(getProperty(property, meta))
        }
        return null
    }

    abstract fun defaultName(): Component
    fun lore(meta: METATYPE): List<Component> {
        val lore = mutableListOf<Component>()

        for(entry in LoreProvider.PROPERTY_PROVIDES_LORE) {
            lore.addAll(entry.getLore(this, meta))
        }

        return InventoryUtils.sanitizeLore(lore)
    }
    protected open fun update(builder: ItemStack.Builder, meta: METATYPE) {
        builder.set(DataComponents.ITEM_NAME, getProperty(ItemProperty.NAME, meta)).lore(lore(meta)).hideExtraTooltip()
        getProperty(ItemProperty.ICON, meta).apply(builder)
        builder.glowing(getProperty(ItemProperty.GLOW, meta))
    }
}
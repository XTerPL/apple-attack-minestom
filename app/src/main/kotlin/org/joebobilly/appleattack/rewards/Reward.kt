package org.joebobilly.appleattack.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.component.DataComponents
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.ItemEntity
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.instance.Instance
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.player.ResolvableProfile
import org.joebobilly.appleattack.items.AAItem
import org.joebobilly.appleattack.items.BasicAAItem
import org.joebobilly.appleattack.items.ItemProperty
import org.joebobilly.appleattack.players.AAPlayer
import org.joebobilly.appleattack.utils.RandomUtils.nextVec2
import java.time.Duration
import kotlin.random.Random

sealed interface Reward {
    companion object {
        fun Reward.toLootTableEntry(multiplierProvider: LootTable.MultiplierProvider = LootTable.MultiplierProvider.Constant(1.0)): LootTable.Entry {
            return LootTable.Entry.Final(this, multiplierProvider)
        }
        fun Reward.toLootTableEntry(minCount: Int, maxCount: Int): LootTable.Entry {
            return this.toLootTableEntry(LootTable.MultiplierProvider.IntMultiplier(minCount, maxCount))
        }
        fun Reward.toLootTableEntry(count: Int = 1): LootTable.Entry {
            return this.toLootTableEntry(LootTable.MultiplierProvider.Constant(count.toDouble()))
        }
        fun Reward.spawnEntities(instance: Instance, position: Pos): List<Entity> {
            val entities = this.createEntities()
            entities.forEach {
                it.setInstance(instance, position)
                it.velocity = Random.nextVec2(2.0, 2.0).add(0.0, 4.0, 0.0)
            }
            return entities
        }

        const val CRATE_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGY4ZDY5NWFlMDVmNmM5MjdjMzVlYzU5NDc0YmEzOTEzYzQ5NmUyNTU0M2JmNThjMjIxMmMyODQ0ZWY2ZTQwIn19fQ=="
        val CRATE_PROFILE = ResolvableProfile(PlayerSkin(CRATE_HEAD_TEXTURE, null))
    }

    class Item<METATYPE> private constructor(
            val type: AAItem<METATYPE>,
            val meta: METATYPE,
            val count: Int
        ) : Reward {
        companion object {
            fun <METATYPE> of(type: AAItem<METATYPE>, meta: METATYPE): Item<METATYPE> {
                return Item(type, meta, 1)
            }
            fun of(type: BasicAAItem): Item<Unit> {
                return of(type, Unit)
            }
        }

        override fun multiply(multiplier: Double): Item<METATYPE> {
            return Item(type, meta, (count * multiplier).toInt())
        }
        override fun grant(player: AAPlayer) {
            val items = getItems()
            for(item in player.inventory.addItemStacks(items, TransactionOption.ALL)) {
                player.dropItem(item)
            }
        }
        override fun createEntities(): List<Entity> {
            val items = getItems()
            return items.map { ItemEntity(it).apply {
                this.setPickupDelay(Duration.ofSeconds(1))
            } }
        }
        override fun getIcon(): ItemStack {
            if(count > type.maxCount) {
                val name = Component.empty()
                    .append(Component.text("${count}x ", NamedTextColor.GRAY))
                    .append(type.getProperty(ItemProperty.NAME, meta))
                val lore = type.lore(meta)

                return ItemStack.builder(Material.PLAYER_HEAD)
                    .set(DataComponents.PROFILE, CRATE_PROFILE)
                    .set(DataComponents.ITEM_NAME, name)
                    .lore(lore).build()
            }
            return type.create(count, meta)
        }

        private fun getItems(): List<ItemStack> {
            val items = mutableListOf<ItemStack>()
            var count = count
            if(count > type.maxCount) {
                val itemStack = type.create(type.maxCount, meta)
                while(count > type.maxCount) {
                    items.add(itemStack)
                    count -= type.maxCount
                }
            }
            if(count > 0) {
                items.add(type.create(count, meta))
            }
            return items
        }
    }

    fun multiply(multiplier: Double): Reward
    fun grant(player: AAPlayer)
    fun createEntities(): List<Entity>
    fun getIcon(): ItemStack
}
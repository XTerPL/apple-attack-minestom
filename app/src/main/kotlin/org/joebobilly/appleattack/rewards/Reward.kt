package org.joebobilly.appleattack.rewards

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.ItemEntity
import net.minestom.server.instance.Instance
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.item.ItemStack
import org.joebobilly.appleattack.items.AAItem
import org.joebobilly.appleattack.items.BasicAAItem
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
        override fun grant(player: AAPlayer, dry: Boolean): Boolean {
            val items = getItems()
            if(player.inventory.addItemStacks(items, TransactionOption.DRY_RUN).contains(false)) {
                return false
            }
            if(!dry) {
                player.inventory.addItemStacks(items, TransactionOption.ALL_OR_NOTHING)
            }
            return true
        }
        override fun createEntities(): List<Entity> {
            val items = getItems()
            return items.map { ItemEntity(it).apply {
                this.setPickupDelay(Duration.ofSeconds(1))
            } }
        }

        private fun getItems(): List<ItemStack> {
            val stacks = count / type.maxCount
            val remainder = count % type.maxCount
            val items = mutableListOf<ItemStack>()
            if(stacks > 0) {
                val itemStack = type.create(type.maxCount, meta)
                (0..stacks).forEach { _ ->
                    items.add(itemStack)
                }
            }
            if(remainder > 0) {
                items.add(type.create(remainder, meta))
            }
            return items
        }
    }

    fun multiply(multiplier: Double): Reward
    fun grant(player: AAPlayer, dry: Boolean = false): Boolean
    fun createEntities(): List<Entity>
}
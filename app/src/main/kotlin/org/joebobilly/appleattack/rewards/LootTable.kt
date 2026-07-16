package org.joebobilly.appleattack.rewards

import kotlin.random.Random

class LootTable {
    private val group = Entry.Group()

    sealed interface Entry {
        class Group : Entry {
            private val entries = mutableListOf<Entry>()
            fun addEntry(entry: Entry): Group {
                entries.add(entry)
                return this
            }
            override fun getRewards(random: Random): List<Reward> {
                val rewards = mutableListOf<Reward>()
                for(entry in entries) {
                    rewards.addAll(entry.getRewards(random))
                }
                return rewards
            }
        }
        class Exclusive : Entry {
            private val entries = mutableListOf<Pair<Entry, Int>>()
            private var totalWeights = 0
            fun addEntry(entry: Entry, weight: Int): Exclusive {
                if(weight <= 0) return this
                entries.add(Pair(entry, weight))
                totalWeights += weight
                return this
            }
            override fun getRewards(random: Random): List<Reward> {
                if(entries.isEmpty()) return emptyList()
                val index = Random.nextInt(totalWeights)
                var currentTotalWeight = 0
                for(entry in entries) {
                    currentTotalWeight += entry.second
                    if(index < currentTotalWeight) {
                        return entry.first.getRewards(random)
                    }
                }
                return emptyList()
            }
        }
        class RandomChance(private val numerator: Int, private val denominator: Int, private val entry: Entry) : Entry {
            override fun getRewards(random: Random): List<Reward> {
                if(random.nextInt(0, denominator) < numerator) {
                    return entry.getRewards(random)
                }
                return emptyList()
            }
        }
        class Final(val reward: Reward, val multiplierProvider: MultiplierProvider) : Entry {
            override fun getRewards(random: Random): List<Reward> {
                return listOf(reward.multiply(multiplierProvider.getMultiplier(random)))
            }
        }

        fun getRewards(random: Random): List<Reward>
    }

    sealed interface MultiplierProvider {
        class Constant(val multiplier: Double) : MultiplierProvider {
            override fun getMultiplier(random: Random): Double = multiplier
        }
        class DoubleRange(val min: Double, val max: Double) : MultiplierProvider {
            override fun getMultiplier(random: Random): Double = random.nextDouble(min, max)
        }
        class IntMultiplier(val min: Int, val max: Int) : MultiplierProvider {
            override fun getMultiplier(random: Random): Double = random.nextInt(min, max+1).toDouble()
        }

        fun getMultiplier(random: Random): Double
    }

    fun addEntry(entry: Entry): LootTable {
        group.addEntry(entry)
        return this
    }

    fun getRewards(): List<Reward> {
        return group.getRewards(Random)
    }
}
package org.joebobilly.appleattack.utils

import net.minestom.server.coordinate.Vec
import kotlin.random.Random

object RandomUtils {
    fun Random.nextVec2(spreadX: Double, spreadZ: Double): Vec {
        val x = nextDouble() * 2 * spreadX - spreadX
        val z = nextDouble() * 2 * spreadZ - spreadZ
        return Vec(x, 0.0, z)
    }

    fun <T> Random.pickFrom(options: Collection<T>): T {
        check(options.isNotEmpty()) { "Cannot pick from an empty collection." }
        val options = options.toList()
        return options[nextInt(options.size)]
    }

    fun <T> Random.pickWithWeights(options: Collection<Pair<T, Double>>): T {
        check(options.isNotEmpty()) { "Cannot pick from an empty collection." }
        val options = options.filter { it.second >= 0.0 }
        val maxWeight = options.map { it.second }.reduce(Double::plus)
        val chosenWeight = nextDouble(maxWeight)
        var encounteredWeight = 0.0
        for(option in options) {
            encounteredWeight += option.second
            if(encounteredWeight > chosenWeight) return option.first
        }
        return options.last().first
    }

    fun <T> Random.pickWithWeights(options: Collection<T>, weightFunction: (T) -> Double): T {
        val options = options.map { Pair(it, weightFunction(it)) }
        return pickWithWeights(options)
    }
}
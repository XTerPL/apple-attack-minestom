package org.joebobilly.appleattack.mobs

import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.ai.GoalSelector
import org.joebobilly.appleattack.utils.RandomUtils.pickWithWeights
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.random.Random

// code adapted from Minestom, cause they don't have any weighing of positions
class HomeStrollGoal(entityCreature: EntityCreature, val wanderRadius: Int,
    val homeRadius: Double = wanderRadius.toDouble(), // radius where positions aren't penalized
    halfLifeDecay: Double = 32.0, // how many blocks it takes for the weight of a position to be halved
    val homePositionGetter: () -> Vec? = { null }
) : GoalSelector(entityCreature) {
    companion object {
        private val DELAY = TimeUnit.MILLISECONDS.toNanos(2500)

        private fun getNearbyBlocks(radius: Int): MutableList<Vec> {
            val blocks: MutableList<Vec> = ArrayList()
            for (x in -radius..radius) {
                for (y in -radius..radius) {
                    for (z in -radius..radius) {
                        blocks.add(Vec(x.toDouble(), y.toDouble(), z.toDouble()))
                    }
                }
            }
            return blocks
        }
    }

    private val closePositions: List<Vec> = getNearbyBlocks(wanderRadius)
    private val decayFactor = 0.5.pow(1/halfLifeDecay)
    private var lastStroll = 0L

    override fun shouldStart(): Boolean {
        return System.nanoTime() - lastStroll >= DELAY
    }

    override fun start() {
        val homePosition = homePositionGetter()
        val targets = closePositions.map {
            val pos = entityCreature.position.add(it)
            Pair(pos, homePosition?.let { getWeight(pos.asVec(), homePosition) } ?: 100.0)
        }

        var remainingAttempt: Int = closePositions.size
        while (remainingAttempt-- > 0) {
            val target = Random.pickWithWeights(targets)
            val result = entityCreature.navigator.setPathTo(target)
            if (result) {
                break
            }
        }
    }

    override fun tick(time: Long) {
    }

    override fun shouldEnd(): Boolean {
        return true
    }

    override fun end() {
        this.lastStroll = System.nanoTime()
    }

    fun getWeight(position: Vec, homePosition: Vec): Double {
        val distance = position.distance(homePosition)

        if(distance <= homeRadius) return 100.0

        return decayFactor.pow(distance - homeRadius) * 100.0
    }
}
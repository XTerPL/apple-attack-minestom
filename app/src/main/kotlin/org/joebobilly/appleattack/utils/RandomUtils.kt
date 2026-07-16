package org.joebobilly.appleattack.utils

import net.minestom.server.coordinate.Vec
import kotlin.random.Random

object RandomUtils {
    fun Random.nextVec2(spreadX: Double, spreadZ: Double): Vec {
        val x = nextDouble() * 2 * spreadX - spreadX
        val z = nextDouble() * 2 * spreadZ - spreadZ
        return Vec(x, 0.0, z)
    }
}
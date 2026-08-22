package org.joebobilly.appleattack.utils

object BitUtils {
    fun toLong(most: Int, least: Int): Long {
        return most.toLong().rotateLeft(32) or least.toLong()
    }
    fun toInts(bits: Long): Pair<Int, Int> {
        val most = bits.rotateRight(32) and 0xFFFFFFFF
        val least = bits and 0xFFFFFFFF
        return most.toInt() to least.toInt()
    }
}
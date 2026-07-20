package org.joebobilly.appleattack.utils

import kotlin.math.max
import kotlin.math.min

object AreaUtils {
    fun withinRectangle(point: Pair<Int, Int>, rectangleCorner1: Pair<Int, Int>, rectangleCorner2: Pair<Int, Int>): Boolean {
        val corner1 = Pair(
            min(rectangleCorner1.first, rectangleCorner2.first),
            min(rectangleCorner1.second, rectangleCorner2.second)
        )
        val corner2 = Pair(
            max(rectangleCorner1.first, rectangleCorner2.first),
            max(rectangleCorner1.second, rectangleCorner2.second)
        )
        if(point.first >= corner1.first && point.first <= corner2.first) {
            if(point.second >= corner1.second && point.second <= corner2.second) {
                return true
            }
        }
        return false
    }
    fun withinRectangle(point: Pair<Int, Int>, rectangle: Pair<Pair<Int, Int>, Pair<Int, Int>>): Boolean {
        return withinRectangle(point, rectangle.first, rectangle.second)
    }
}
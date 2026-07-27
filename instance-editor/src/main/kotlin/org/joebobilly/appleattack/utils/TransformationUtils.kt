package org.joebobilly.appleattack.utils

import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f

object TransformationUtils {
    val ZERO_VECTOR = Vector3f()
    val ONE_VECTOR = Vector3f(1f)
    val ZERO_ROTATION = AxisAngle4f()

    fun scale(vector: Vector3f): Transformation {
        return Transformation(ZERO_VECTOR, ZERO_ROTATION, vector, ZERO_ROTATION)
    }
}
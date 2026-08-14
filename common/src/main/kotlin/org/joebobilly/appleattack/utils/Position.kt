package org.joebobilly.appleattack.utils

import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTCopySerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes

data class Position(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float) {
    // this has to exist for some reason???
    companion object;

    constructor(x: Double, y: Double, z: Double) : this(x, y, z, 0f, 0f)

    object Serializer : NBTCopySerializer<Position> {
        private val x = SerializationTypes.DOUBLE.toEntry("x")
        private val y = SerializationTypes.DOUBLE.toEntry("y")
        private val z = SerializationTypes.DOUBLE.toEntry("z")
        private val yaw = SerializationTypes.FLOAT.toEntry("yaw")
        private val pitch = SerializationTypes.FLOAT.toEntry("pitch")

        override val klass = Position::class

        override fun read(context: DeserializationContext): Position {
            val x = context.read(x, 0.0)
            val y = context.read(y, 0.0)
            val z = context.read(z, 0.0)
            val yaw = context.read(yaw, 0f)
            val pitch = context.read(pitch, 0f)
            return Position(x, y, z, yaw, pitch)
        }

        override fun write(context: SerializationContext, value: Position) {
            context.write(x, value.x)
            context.write(y, value.y)
            context.write(z, value.z)
            context.write(yaw, value.yaw)
            context.write(pitch, value.pitch)
        }

        override fun copy(value: Position): Position {
            return value.copy()
        }
    }
}
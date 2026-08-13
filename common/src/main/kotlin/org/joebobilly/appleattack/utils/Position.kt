package org.joebobilly.appleattack.utils

import net.minestom.server.coordinate.Pos
import org.bukkit.Location
import org.bukkit.World
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTCopySerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes

data class Position(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float) {
    companion object {
        fun fromPaperLocation(location: Location)
            = Position(location.x, location.y, location.z, location.yaw, location.pitch)
        fun fromMinestomPos(pos: Pos)
            = Position(pos.x, pos.y, pos.z, pos.yaw, pos.pitch)
    }

    constructor(x: Double, y: Double, z: Double) : this(x, y, z, 0f, 0f)

    fun toPaperLocation(world: World): Location {
        return Location(world, this.x, this.y, this.z, this.yaw, this.pitch)
    }
    fun toMinestomPos(): Pos {
        return Pos(this.x, this.y, this.z, this.yaw, this.pitch)
    }

    object Serializer : NBTCopySerializer<Position>(Position::class) {
        private val x = SerializationTypes.DOUBLE.toEntry("x")
        private val y = SerializationTypes.DOUBLE.toEntry("y")
        private val z = SerializationTypes.DOUBLE.toEntry("z")
        private val yaw = SerializationTypes.FLOAT.toEntry("yaw")
        private val pitch = SerializationTypes.FLOAT.toEntry("pitch")

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
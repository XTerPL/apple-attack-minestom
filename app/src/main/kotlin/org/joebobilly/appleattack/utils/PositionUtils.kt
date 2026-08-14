package org.joebobilly.appleattack.utils

import net.minestom.server.coordinate.Pos

object PositionUtils {
    fun Position.toMinestomPos(): Pos {
        return Pos(this.x, this.y, this.z, this.yaw, this.pitch)
    }
    fun Position.Companion.fromMinestomPos(pos: Pos): Position {
        return Position(pos.x, pos.y, pos.z, pos.yaw, pos.pitch)
    }
}
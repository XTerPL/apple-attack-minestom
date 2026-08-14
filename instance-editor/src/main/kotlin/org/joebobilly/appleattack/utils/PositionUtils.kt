package org.joebobilly.appleattack.utils

import org.bukkit.Location
import org.bukkit.World

object PositionUtils {
    fun Position.toPaperLocation(world: World): Location {
        return Location(world, this.x, this.y, this.z, this.yaw, this.pitch)
    }
    fun Position.Companion.fromPaperLocation(location: Location): Position {
        return Position(location.x, location.y, location.z, location.yaw, location.pitch)
    }
}
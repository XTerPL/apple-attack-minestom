package org.joebobilly.appleattack.utils

import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.joebobilly.appleattack.InstanceEditor

object SchedulerUtils {
    fun BukkitScheduler.runTaskLater(delay: Long, runnable: (BukkitTask) -> Unit) {
        this.runTaskLater(InstanceEditor.get(), runnable, delay)
    }
}
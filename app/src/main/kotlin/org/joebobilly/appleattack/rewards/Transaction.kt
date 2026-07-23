package org.joebobilly.appleattack.rewards

import org.joebobilly.appleattack.players.AAPlayer

class Transaction(val reward: Reward, val cost: Cost) {
    fun apply(player: AAPlayer): Boolean {
        if(!cost.take(player)) return false
        reward.grant(player)
        return true
    }
}
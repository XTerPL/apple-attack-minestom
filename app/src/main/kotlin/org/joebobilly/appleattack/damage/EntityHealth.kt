package org.joebobilly.appleattack.damage

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.LivingEntity
import net.minestom.server.network.packet.server.play.DamageEventPacket
import org.joebobilly.appleattack.mobs.AAMob
import org.joebobilly.appleattack.mobs.AAMobType
import org.joebobilly.appleattack.players.AAPlayer

class EntityHealth(val entity: LivingEntity, val maxHealth: () -> Double) {
    private var health = maxHealth()
        set(value) {
            field = value
            updateBossBar()
        }
    val bossBar = BossBar.bossBar(Component.text(""), 1f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10)

    init {
        updateBossBar()
    }

    fun getHealth(): Double = health

    private fun damageSound(): Sound {
        if(entity is AAMob) return entity.type.damageSound()
        return AAMobType.defaultDamageSound
    }

    fun dealDamage(damage: DamageInfo) {
        if(entity.isDead) return
        health -= damage.attackInfo.damage * damage.attackStrength
        if(health <= 0) {
            Audiences.players().hideBossBar(bossBar)
            entity.kill()
            return
        }
        val attacker = damage.attacker
        if(attacker is AAPlayer) {
            attacker.trackedHealth = this
        }
        val source = damage.source
        if(damage.attackInfo.knockback > 0 && source != null) {
            val knockbackDir = source.position.asVec().sub(entity.position).mul(1.0, 0.0, 1.0).normalize()
            entity.takeKnockback(damage.attackInfo.knockback, knockbackDir.x, knockbackDir.z)
        }
        entity.sendPacketToViewersAndSelf(DamageEventPacket(
            entity.entityId,
            MinecraftServer.getDamageTypeRegistry().getId(damage.attackInfo.damageType),
            damage.attacker?.entityId?.apply { this + 1 } ?: 0,
            damage.source?.entityId?.apply { this + 1 } ?: 0,
            damage.source?.position
        ))
        entity.instance.filterAudience { it != entity }.playSound(damageSound(), entity)
    }

    fun heal(regain: Double) {
        if(health > maxHealth()) {
            return
        }
        health = (health + regain).coerceAtMost(maxHealth())
    }

    fun healFull() {
        health = maxHealth()
    }

    fun getHealthDisplay(): Component {
        return Component.text("${health.toInt()}/${maxHealth().toInt()}❤", NamedTextColor.RED)
    }

    fun updateBossBar() {
        val name = entity.get(DataComponents.CUSTOM_NAME) ?:
            Component.text("Unknown", NamedTextColor.WHITE).decorate(TextDecoration.BOLD, TextDecoration.OBFUSCATED)
        val healthDisplay = getHealthDisplay()
        val title = Component.empty()
            .append(name)
            .append(Component.text(" | ", NamedTextColor.DARK_RED))
            .append(healthDisplay)
        val progress = (health / maxHealth()).coerceIn(0.0, 1.0)
        bossBar.name(title)
        bossBar.progress(progress.toFloat())
    }
}
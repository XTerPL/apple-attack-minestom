package org.joebobilly.appleattack.entities.type

import net.kyori.adventure.text.Component
import net.minestom.server.component.DataComponents
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

sealed class AAEntityType<ENTITY : Entity>(val id: String, val startingEntityType: EntityType) {
    abstract fun entityName(): Component
    open fun despawnRadius(): Double? = 32.0

    private fun init(entity: ENTITY) {
        entity.set(DataComponents.CUSTOM_NAME, entityName())
        onInit(entity)
    }

    protected open fun onInit(entity: ENTITY) {

    }

    protected open fun beforeSpawn(entity: ENTITY, instance: Instance, position: Pos) {

    }

    protected abstract fun createUninitialized(): ENTITY
    fun create(): ENTITY {
        return createUninitialized().apply {
            init(this)
        }
    }

    fun spawn(instance: Instance, position: Pos, thenRun: ((ENTITY) -> Unit)? = null): ENTITY? {
        if(shouldDespawn(instance, position)) {
            return null
        }
        val entity = create()
        beforeSpawn(entity, instance, position)
        entity.setInstance(instance, position).thenRun {
            thenRun?.invoke(entity)
        }
        return entity
    }

    fun shouldDespawn(instance: Instance, position: Pos): Boolean {
        val despawnRadius = despawnRadius() ?: return false
        return instance.getNearbyEntities(position, despawnRadius).none { it is Player }
    }
}
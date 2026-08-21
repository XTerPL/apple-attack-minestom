package org.joebobilly.appleattack.infodump

import net.kyori.adventure.text.Component
import org.joebobilly.appleattack.entities.EntityTypeClass
import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTSerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.serialization.SerializationTypes

data class EntityDumpEntry(val id: String, val entityName: Component, val startingEntityTypeId: String, val entityClass: EntityTypeClass) {
    object Serializer : NBTSerializer<EntityDumpEntry> {
        val id = SerializationTypes.STRING.toEntry("id")
        val defaultName = SerializationTypes.COMPONENT.toEntry("default_name")
        val startingEntityTypeId = SerializationTypes.STRING.toEntry("starting_entity_type_id")
        val entityClass = SerializationTypes.enum<EntityTypeClass>().toEntry("entity_class")

        override val klass = EntityDumpEntry::class
        override fun read(context: DeserializationContext): EntityDumpEntry {
            val id = context.read(id)
            val defaultName = context.read(defaultName)
            val startingEntityTypeId = context.read(startingEntityTypeId)
            val entityClass = context.read(entityClass)
            return EntityDumpEntry(id, defaultName, startingEntityTypeId, entityClass)
        }
        override fun write(context: SerializationContext, value: EntityDumpEntry) {
            context.write(id, value.id)
            context.write(defaultName, value.entityName)
            context.write(startingEntityTypeId, value.startingEntityTypeId)
            context.write(entityClass, value.entityClass)
        }
    }
}
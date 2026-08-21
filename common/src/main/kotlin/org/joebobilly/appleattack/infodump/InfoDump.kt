package org.joebobilly.appleattack.infodump

import org.joebobilly.appleattack.serialization.DeserializationContext
import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.serialization.NBTSerializer
import org.joebobilly.appleattack.serialization.SerializationContext
import org.joebobilly.appleattack.serialization.SerializationType.Companion.list
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry

class InfoDump {
    val entityTypes = mutableListOf<EntityDumpEntry>()

    fun getEntityType(id: String) = entityTypes.find { it.id == id }

    object Serializer : NBTSerializer<InfoDump> {
        val entityTypes = EntityDumpEntry.Serializer.list().toEntry("entity_types")
        override val klass = InfoDump::class

        override fun read(context: DeserializationContext): InfoDump {
            val dump = InfoDump()
            dump.entityTypes.addAll(context.read(entityTypes))
            return dump
        }

        override fun write(
            context: SerializationContext,
            value: InfoDump
        ) {
            context.write(entityTypes, value.entityTypes)
        }
    }
}
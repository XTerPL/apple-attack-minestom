package org.joebobilly.appleattack.serialization

object UnitNBTSerializer : NBTCopySerializer<Unit> {
    override val klass = Unit::class

    override fun read(context: DeserializationContext) {}
    override fun write(context: SerializationContext, value: Unit) {}
    override fun copy(value: Unit) {}
}
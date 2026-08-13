package org.joebobilly.appleattack.serialization

object UnitNBTSerializer : NBTCopySerializer<Unit>(Unit::class) {
    override fun read(context: DeserializationContext) {}
    override fun write(context: SerializationContext, value: Unit) {}
    override fun copy(value: Unit) {}
}
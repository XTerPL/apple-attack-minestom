package org.joebobilly.appleattack.serialization

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import kotlin.reflect.KClass

class MappedPersistentDataType<O : Any, P : Any, C : Any>(
    private val base: PersistentDataType<O, P>, private val klass: KClass<C>,
    private val readMap: (P) -> C, private val writeMap: (C) -> P
) : PersistentDataType<O, C> {
    override fun getPrimitiveType(): Class<O> = base.primitiveType
    override fun getComplexType(): Class<C> = klass.java
    override fun toPrimitive(complex: C, context: PersistentDataAdapterContext)
            = base.toPrimitive(writeMap(complex), context)
    override fun fromPrimitive(primitive: O, context: PersistentDataAdapterContext)
            = readMap(base.fromPrimitive(primitive, context))
}
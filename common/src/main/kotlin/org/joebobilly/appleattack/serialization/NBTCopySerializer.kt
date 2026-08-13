package org.joebobilly.appleattack.serialization

import kotlin.reflect.KClass

abstract class NBTCopySerializer<T : Any>(klass : KClass<T>) : NBTSerializer<T>(klass) {
    abstract fun copy(value: T): T
}
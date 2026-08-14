package org.joebobilly.appleattack.serialization

interface NBTCopySerializer<T : Any> : NBTSerializer<T> {
    fun copy(value: T): T
}
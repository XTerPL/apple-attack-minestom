package org.joebobilly.appleattack.serialization

import org.joebobilly.appleattack.serialization.DeserializationContext.Companion.read
import org.joebobilly.appleattack.utils.ClassUtils

class PairNBTSerializer<A : Any, B : Any>(
    private val firstEntry: SerializationEntry<A>,
    private val secondEntry: SerializationEntry<B>
) : NBTSerializer<Pair<A, B>> {
    init {
        require(firstEntry.key != secondEntry.key) { "The keys of both the first and second entries cannot be the same!" }
    }

    override val klass = ClassUtils.getKClass<Pair<A, B>>()
    override fun read(context: DeserializationContext): Pair<A, B> {
        val first = context.read(firstEntry)
        val second = context.read(secondEntry)
        return Pair(first, second)
    }
    override fun write(context: SerializationContext, value: Pair<A, B>) {
        context.write(firstEntry, value.first)
        context.write(secondEntry, value.second)
    }
}
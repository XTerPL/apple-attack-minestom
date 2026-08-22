package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.InvalidKeyException
import net.kyori.adventure.key.Key
import org.joebobilly.appleattack.serialization.SerializationType.Companion.map
import org.joebobilly.appleattack.serialization.SerializationType.Companion.toEntry
import org.joebobilly.appleattack.utils.BitUtils
import org.joebobilly.appleattack.utils.CollectionUtils.map
import org.joebobilly.appleattack.utils.ComponentUtils
import org.joebobilly.appleattack.utils.ComponentUtils.toMiniMessage
import org.joebobilly.appleattack.utils.KeyUtils
import org.joebobilly.appleattack.utils.Position
import kotlin.text.uppercase
import kotlin.uuid.Uuid

object SerializationTypes {
    // integers
    val BYTE = PrimitiveSerializationType.ByteType
    val SHORT = PrimitiveSerializationType.ShortType
    val INTEGER = PrimitiveSerializationType.IntegerType
    val LONG = PrimitiveSerializationType.LongType

    // integer arrays
    val BYTE_ARRAY = PrimitiveSerializationType.ByteArrayType
    val INTEGER_ARRAY = PrimitiveSerializationType.IntArrayType
    val LONG_ARRAY = PrimitiveSerializationType.LongArrayType

    // decimals
    val FLOAT = PrimitiveSerializationType.FloatType
    val DOUBLE = PrimitiveSerializationType.DoubleType

    // strings
    val STRING = PrimitiveSerializationType.StringType
    val KEY = STRING.map({
        try {
            return@map Key.key(it)
        }
        catch(e: InvalidKeyException) {
            throw NBTReadError("", "invalid key '$it'", e)
        }
    }, Key::asString)
    val COMPONENT = STRING.map(ComponentUtils::fromMiniMessage) { it.toMiniMessage() }

    // other
    val BOOLEAN = PrimitiveSerializationType.BooleanType
    val UNIT = UnitNBTSerializer
    val POSITION = Position.Serializer
    val UUID = INTEGER_ARRAY.map(
        {
            NBTReadError.checkOrThrow(it.size == 4) { "UUID must be a 4 sized integer array" }
            val mostSignificant = BitUtils.toLong(it[0], it[1])
            val leastSignificant = BitUtils.toLong(it[2], it[3])
            Uuid.fromLongs(mostSignificant, leastSignificant)
        },
        {
            val bits = it.toLongs { most, least -> most to least }.map { long -> BitUtils.toInts(long) }
            listOf(bits.first.first, bits.first.second, bits.second.first, bits.second.second).toIntArray()
        }
    )

    inline fun <reified T : Enum<T>> enum() = STRING.map({
        try {
            return@map enumValueOf<T>(it.uppercase())
        }
        catch(_: IllegalArgumentException) {
            throw NBTReadError("", "invalid enum value '$it'")
        }
    }, { it.name.lowercase() })

    fun <A : Any, B : Any> pair(firstType: SerializationType<A>, secondType: SerializationType<B>)
        = pair(firstType, secondType, "first", "second")

    fun <K : Any, V : Any> map(valueType: SerializationType<V>, keyReadMap: (Key) -> K, keyWriteMap: (K) -> Key)
        = MapNBTSerializer(valueType, keyReadMap, keyWriteMap)
    fun <T : Any> map(valueType: SerializationType<T>)
        = map(valueType, { it }, { it })
    fun <T : Any> mapUUID(valueType: SerializationType<T>)
        = map(valueType, { Uuid.parseHexDash(it.value()) }, { KeyUtils.of(it.toHexDashString()) })

    private fun <A : Any, B : Any> pair(
        firstType: SerializationType<A>, secondType: SerializationType<B>,
        firstName: String, secondName: String
    ): PairNBTSerializer<A, B> {
        require(firstName != secondName) { "The two names of a pair serialization type cannot be the same!" }
        return PairNBTSerializer(firstType.toEntry(firstName), secondType.toEntry(secondName))
    }
}
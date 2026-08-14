package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.InvalidKeyException
import net.kyori.adventure.key.Key
import org.joebobilly.appleattack.serialization.SerializationType.Companion.map
import org.joebobilly.appleattack.utils.Position
import kotlin.text.uppercase

object SerializationTypes {
    // integers
    val BYTE = PrimitiveSerializationType.ByteType
    val SHORT = PrimitiveSerializationType.ShortType
    val INTEGER = PrimitiveSerializationType.IntegerType
    val LONG = PrimitiveSerializationType.LongType

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

    // other
    val BOOLEAN = PrimitiveSerializationType.BooleanType
    val UNIT = UnitNBTSerializer
    val POSITION = Position.Serializer

    inline fun <reified T : Enum<T>> enum() = STRING.map({
        try {
            return@map enumValueOf<T>(it.uppercase())
        }
        catch(_: IllegalArgumentException) {
            throw NBTReadError("", "invalid enum value '$it'")
        }
    }, { it.name.lowercase() })
}
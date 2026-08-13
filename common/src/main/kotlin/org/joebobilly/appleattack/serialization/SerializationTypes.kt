package org.joebobilly.appleattack.serialization

import net.kyori.adventure.key.InvalidKeyException
import net.kyori.adventure.key.Key
import net.minestom.server.tag.Tag
import org.bukkit.persistence.PersistentDataType
import org.joebobilly.appleattack.serialization.SerializationType.Companion.map
import org.joebobilly.appleattack.utils.Position

object SerializationTypes {
    // integers
    val BYTE = object : SerializationType<Byte> {
        override fun toMinestomTag(key: Key) = Tag.Byte(key.toString())
        override fun toPersistentType() = PersistentDataType.BYTE
    }
    val SHORT = object : SerializationType<Short> {
        override fun toMinestomTag(key: Key) = Tag.Short(key.toString())
        override fun toPersistentType() = PersistentDataType.SHORT
    }
    val INTEGER = object : SerializationType<Int> {
        override fun toMinestomTag(key: Key) = Tag.Integer(key.toString())
        override fun toPersistentType() = PersistentDataType.INTEGER
    }
    val LONG = object : SerializationType<Long> {
        override fun toMinestomTag(key: Key) = Tag.Long(key.toString())
        override fun toPersistentType() = PersistentDataType.LONG
    }

    // decimals
    val FLOAT = object : SerializationType<Float> {
        override fun toMinestomTag(key: Key) = Tag.Float(key.toString())
        override fun toPersistentType() = PersistentDataType.FLOAT
    }
    val DOUBLE = object : SerializationType<Double> {
        override fun toMinestomTag(key: Key) = Tag.Double(key.toString())
        override fun toPersistentType() = PersistentDataType.DOUBLE
    }

    // strings
    val STRING = object : SerializationType<String> {
        override fun toMinestomTag(key: Key) = Tag.String(key.toString())
        override fun toPersistentType() = PersistentDataType.STRING
    }
    val KEY = STRING.map({
        try {
            return@map Key.key(it)
        }
        catch(e: InvalidKeyException) {
            throw NBTReadError("", "invalid key '$it'", e)
        }
    }, Key::asString)

    // other
    val BOOLEAN = object : SerializationType<Boolean> {
        override fun toMinestomTag(key: Key) = Tag.Boolean(key.toString())
        override fun toPersistentType() = PersistentDataType.BOOLEAN
    }
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
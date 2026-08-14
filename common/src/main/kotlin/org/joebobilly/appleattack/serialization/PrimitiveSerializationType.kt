package org.joebobilly.appleattack.serialization

sealed interface PrimitiveSerializationType<T : Any> : SerializationType<T> {
    object ByteType : PrimitiveSerializationType<Byte>
    object ShortType : PrimitiveSerializationType<Short>
    object IntegerType : PrimitiveSerializationType<Int>
    object LongType : PrimitiveSerializationType<Long>
    object FloatType : PrimitiveSerializationType<Float>
    object DoubleType : PrimitiveSerializationType<Double>
    object StringType : PrimitiveSerializationType<String>
    object BooleanType : PrimitiveSerializationType<Boolean>
}
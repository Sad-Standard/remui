/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package org.remui

import org.remui.util.makeKType
import kotlinx.serialization.*
import kotlinx.serialization.builtins.NothingSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.*
import kotlin.time.Duration

//@Serializable
//data class SerializedData private constructor(
//    private val binary: ByteArray? = null,
//    private val text  : String?    = null
//)
//{
//
//    companion object {
//        operator fun invoke(binary: ByteArray) = SerializedData(binary = binary)
//        operator fun invoke(text  : String   ) = SerializedData(text   = text  )
//    }
//
//    fun asBinary(): ByteArray = when {
//        binary != null && text != null -> error("")
//        binary != null                 -> binary
//        text != null                   -> text.encodeToByteArray()
//        else                           -> error("")
//    }
//
//    fun asText(): String = when {
//        binary != null && text != null -> error("")
//        binary != null                 -> binary.decodeToString()
//        text != null                   -> text
//        else                           -> error("")
//    }
//}


@Serializable
class SerializedKType (
    val classId  : Int,
    val nullable : Boolean,
    val arguments: List<SerializedKTypeProjection>,
)

@Serializable
class SerializedKTypeProjection (
    val variance: SerializedKVariance?,
    val type    : SerializedKType?
)

@Serializable
enum class SerializedKVariance { INVARIANT, IN, OUT, }

fun ObjectPool.encode(kType: KType): SerializedKType {
    return SerializedKType(
        nullable  = kType.isMarkedNullable,
        classId   = this[classKey, (kType.classifier as KClass<*>)],
        arguments = kType.arguments.map { encode(it) }
    )
}

fun ObjectPool.encode(kProjection: KTypeProjection): SerializedKTypeProjection{
    return SerializedKTypeProjection(
        variance = when(kProjection.variance){
            KVariance.IN        -> SerializedKVariance.IN
            KVariance.OUT       -> SerializedKVariance.OUT
            KVariance.INVARIANT -> SerializedKVariance.INVARIANT
            null -> null
        },
        type = kProjection.type?.let { encode(it) }
    )
}

fun ObjectPool.decode(serializedKType: SerializedKType): KType {
    val kClass = this[classKey, serializedKType.classId]
    return makeKType(
        serializedKType.arguments.map { decode(it) },
        kClass,
        serializedKType.nullable
    )
}

fun ObjectPool.decode(serializedKTypeProjection: SerializedKTypeProjection): KTypeProjection {
    return KTypeProjection(
        variance = when (serializedKTypeProjection.variance) {
            SerializedKVariance.IN        -> KVariance.IN
            SerializedKVariance.OUT       -> KVariance.OUT
            SerializedKVariance.INVARIANT -> KVariance.INVARIANT
            null -> null
        },
        type = serializedKTypeProjection.type?.let { decode(it) }
    )
}



val BaseClassModule = ClassModule(
    Any::class, Nothing::class, Unit::class,
    Boolean::class,
    Char::class,
    Byte::class, Short::class, Int::class, Long::class, Float::class, Double::class,
    Number::class,
    String::class,
    Duration::class,
    List::class,
    Set::class,
    Map::class,
    Map.Entry::class,
    Pair::class, Triple::class,
    SerializedKType::class, SerializedKTypeProjection::class, SerializedKVariance::class,
    ByteArray::class,
    RStruct::class, RStruct.Impl::class,
    ServerUpdate::class, AddDiff::class, PropDiff::class, RemoveDiff::class,
    InteractionBlockMessage::class, InteractionMessage::class,
)

fun buildSerializer(
    objectPool: ObjectPool,
    toInt  : (RStruct) -> Int,
    fromInt: (Int) -> RStruct
): SerializersModule {
    return SerializersModule {
        fun kSerializerFor(kClass: KClass<out RStruct>): KSerializer<RStruct> {
            return object: KSerializer<RStruct> {
                override val descriptor: SerialDescriptor get() = PrimitiveSerialDescriptor("r", PrimitiveKind.INT)
                override fun serialize  (encoder: Encoder, value: RStruct) = encoder.encodeInt(toInt(value))
                override fun deserialize(decoder: Decoder): RStruct = fromInt(decoder.decodeInt())
            }
        }

        fun forClass(kClass: KClass<out RStruct>){
            val ks = kSerializerFor(kClass)
            contextual(kClass as KClass<RStruct>, kSerializerFor(kClass))
//            polymorphicDefaultSerializer(kClass){ obj -> ks }
//            polymorphicDefaultDeserializer(kClass as KClass<RStruct>){ obj -> ks as KSerializer<out RStruct> }
        }

        forClass(RStruct::class)
        objectPool.getAll(remuiInterfaceCategory).forEach { kClass -> forClass(kClass) }
        objectPool.getAll(remuiComponentCategory).forEach { kClass ->
            contextual(kClass as KClass<RStruct>, kSerializerFor(kClass))
        }

        polymorphicDefaultSerializer(Any::class) { NothingSerializer() as KSerializer<Any?> }
        polymorphicDefaultDeserializer(Any::class) { NothingSerializer() as KSerializer<Any> }




    }

}

/**
 * TODO Document
 */
interface RemuiSerializer {

    /**
     * TODO Document
     */
    val module: SerializersModule

    /**
     * TODO Document
     */
    fun encode(type: KType, value: Any?): ByteArray

    /**
     * TODO Document
     */
    fun decode(type: KType, data: ByteArray): Any?

}

/**
 * TODO Document
 */
inline fun <reified T> RemuiSerializer.encode(value: T): ByteArray = this.encode(typeOf<T>(), value)

/**
 * TODO Document
 */
inline fun <reified T> RemuiSerializer.decode(data: ByteArray): T  = this.decode(typeOf<T>(), data) as T






/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package com.gattagdev.remui

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.NothingSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.typeOf
import kotlin.time.Duration

@Serializable
sealed interface SerializedData {

    @SerialName("t")
    @Serializable
    data class Text(val value: String): SerializedData

    @SerialName("b")
    @Serializable
    data class Binary(val value: ByteArray): SerializedData

    @SerialName("j")
    @Serializable
    data class Json(val value: JsonElement): SerializedData

}

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

class ObjectPoolCategory<T: Any>

interface ObjectPoolBuilderSpec {
    fun <T: Any> include(category: ObjectPoolCategory<T>, objects: Iterable<T>)
}

interface ObjectModule {

    context(ObjectPoolBuilderSpec)
    fun construct()

    data class Concatenated(val modules: List<ObjectModule>): ObjectModule {

        context(ObjectPoolBuilderSpec)
        override fun construct() { modules.forEach { it.construct() } }

    }

    operator fun plus(right: ObjectModule): ObjectModule = Concatenated(listOf(this, right))
}



class ObjectPool private constructor(
    private val categories: Map<ObjectPoolCategory<*>, Pair<List<Any>, Map<Any, Int>>>
) {

    private operator fun <T: Any> get(category: ObjectPoolCategory<T>) =
        (categories[category] ?: error("Category not present in pool")) as Pair<List<T>, Map<T, Int>>

    operator fun <T: Any> get(category: ObjectPoolCategory<T>, index: Int): T =
        this[category].first.getOrNull(index) ?: error("Index not found in category")

    operator fun <T: Any> get(category: ObjectPoolCategory<T>, obj: T): Int =
        this[category].second[obj] ?: error("Object ($obj) not found in category")

    fun <T: Any> getAll(category: ObjectPoolCategory<T>): List<T> =
        this[category].first

    companion object {
        operator fun invoke(vararg modules: ObjectModule): ObjectPool {
            val categories = mutableMapOf<ObjectPoolCategory<*>, Pair<MutableList<Any>, MutableMap<Any, Int>>>()

            val builder = object: ObjectPoolBuilderSpec {
                override fun <T: Any> include(category: ObjectPoolCategory<T>, objects: Iterable<T>) {
                    val pair = categories.getOrPut(category){ Pair(mutableListOf(), mutableMapOf()) }
                    objects.forEach { obj ->
                        if(obj !in pair.second) {
                            pair.second[obj] = pair.first.size
                            pair.first += obj
                        }
                    }
                }
            }

            modules.forEach { module -> with(builder) { module.construct() } }

            return ObjectPool(categories)
        }
    }
}

val classKey = ObjectPoolCategory<KClass<*>>()

class ClassModule(
    private vararg val classes: KClass<*>
): ObjectModule {
    context(ObjectPoolBuilderSpec)
    override fun construct() {
        include(classKey, classes.asIterable())
    }
}

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

expect fun makeKType(
    arguments: List<KTypeProjection>,
    classifier: KClassifier?,
    isMarkedNullable: Boolean
): KType

fun substituteTypeParameters(type: KType, substitutions: Map<String, KType>): KType {
    val cls = type.classifier
    if (cls is KTypeParameter) substitutions[cls.name]?.let { return it }

    val newArguments = type.arguments.map { arg ->
        val ot = arg.type
        if (ot != null) KTypeProjection(arg.variance, substituteTypeParameters(ot, substitutions))
        else arg
    }

    return makeKType(newArguments, type.classifier, type.isMarkedNullable)
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
    SerializedData::class, SerializedData.Json::class, SerializedData.Text::class, SerializedData.Binary::class,
    DynamicValue::class
)

val functionClasses = arrayOf(CFunction::class, CFunction1::class, CFunction2::class, CFunction3::class)

val RemuiClassModule = ClassModule(
    RemuiStruct::class, RemuiStruct.Impl::class,
    ServerUpdate::class, AddDiff::class, PropDiff::class, RemoveDiff::class,
    InteractionBlockMessage::class, InteractionMessage::class,
    Computable::class, Computable.Internal::class, Computable.Const::class, Computable.FunctionCall::class,
    *functionClasses
) + CBaseFunctions

fun buildSerializer(
    objectPool: ObjectPool,
    toLong  : (RemuiStruct) -> Long,
    fromLong: (Long) -> RemuiStruct
): SerializersModule {
    return SerializersModule {
        fun kSerializerFor(kClass: KClass<out RemuiStruct>): KSerializer<RemuiStruct> {
            return object: KSerializer<RemuiStruct> {
                override val descriptor: SerialDescriptor get() = PrimitiveSerialDescriptor("r", PrimitiveKind.LONG)

                override fun serialize(encoder: Encoder, value: RemuiStruct) = encoder.encodeLong(toLong(value))

                override fun deserialize(decoder: Decoder): RemuiStruct = fromLong(decoder.decodeLong())
            }
        }


        fun forClass(kClass: KClass<out RemuiStruct>){
            val ks = kSerializerFor(kClass)
            polymorphicDefaultSerializer(kClass){ obj -> ks }
            polymorphicDefaultDeserializer(kClass as KClass<RemuiStruct>){ obj -> ks as KSerializer<out RemuiStruct> }
        }
        forClass(RemuiStruct::class)
        objectPool.getAll(remuiInterfaceCategory).forEach { kClass -> forClass(kClass) }
        objectPool.getAll(remuiComponentCategory).forEach { kClass ->
            contextual(kClass as KClass<RemuiStruct>, kSerializerFor(kClass))
        }
        val skt = typeOf<SerializedKType>()

        polymorphicDefaultSerializer(Any::class){ NothingSerializer() as KSerializer<Any?> }
        polymorphicDefaultDeserializer(Any::class){ NothingSerializer() as KSerializer<Any> }

        val dv = object: KSerializer<DynamicValue> {
            override val descriptor: SerialDescriptor = buildClassSerialDescriptor("dv"){
                element<SerializedKType>("type")
                element<Unit?>("value")
            }
            override fun serialize(encoder: Encoder, value: DynamicValue) {
                val sm = encoder.serializersModule
                encoder.encodeStructure(descriptor){
                    encodeSerializableElement        (descriptor, 0, sm.serializer(skt)       , objectPool.encode(value.type))
                    encodeNullableSerializableElement(descriptor, 1, sm.serializer(value.type), value.value                 )
                }
            }
            override fun deserialize(decoder: Decoder): DynamicValue {
                val sm = decoder.serializersModule
                return decoder.decodeStructure(descriptor){
                    check(decodeElementIndex(descriptor) == 0)
                    val sType = decodeSerializableElement<SerializedKType>(descriptor, 0, sm.serializer(skt) as KSerializer<SerializedKType>)
                    val kType = objectPool.decode(sType)
                    check(decodeElementIndex(descriptor) == 1)
                    val value = decodeNullableSerializableElement<Any>(descriptor, 1, sm.serializer(kType))
                    check(decodeElementIndex(descriptor) == DECODE_DONE)
                    DynamicValue(kType, value)
                }
            }

        }
        polymorphicDefaultSerializer  (DynamicValue::class) { value -> dv }
        polymorphicDefaultDeserializer(DynamicValue::class) { value -> dv }

        val cf = object: KSerializer<CFunction<*>> {
            override val descriptor: SerialDescriptor get() = PrimitiveSerialDescriptor("cf", PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: CFunction<*>) = encoder.encodeInt(objectPool[functionCategory, value])

            override fun deserialize(decoder: Decoder): CFunction<*> = objectPool[functionCategory, decoder.decodeInt()]
        }

        functionClasses.forEach { klass ->
            polymorphicDefaultSerializer(klass) { value -> cf }
            polymorphicDefaultDeserializer(klass as KClass<CFunction<*>>) { value -> cf }
        }

    }

}


interface RemuiSerializer {
    val module: SerializersModule

    fun encode(type: KType, value: Any?): SerializedData

    fun decode(type: KType, data: SerializedData): Any?
}

inline fun <reified T> RemuiSerializer.encode(value: T): SerializedData = this.encode(typeOf<T>(), value)

inline fun <reified T> RemuiSerializer.decode(data: SerializedData): T  = this.decode(typeOf<T>(), data) as T


class RemuiJsonSerializer(override val module: SerializersModule): RemuiSerializer{
    private val json = Json {
        serializersModule = module
        useArrayPolymorphism = true
    }

    override fun encode(type: KType, value: Any?): SerializedData {
        val encoded =  json.encodeToJsonElement(module.serializer(type), value)
        return SerializedData.Json(encoded)
    }

    override fun decode(type: KType, data: SerializedData): Any? {
        val s = module.serializer(type)
        return when(data){
            is SerializedData.Json   -> json.decodeFromJsonElement(s, data.value)
            is SerializedData.Text   -> json.decodeFromString(s, data.value)
            is SerializedData.Binary -> json.decodeFromString(s, data.value.decodeToString())
        }
    }
}

class RemuiProtobufSerializer(override val module: SerializersModule): RemuiSerializer{

    private val protobuf = kotlinx.serialization.protobuf.ProtoBuf {
        serializersModule = module
    }
    override fun encode(type: KType, value: Any?): SerializedData {
        val encoded =  protobuf.encodeToByteArray(module.serializer(type), value)
        return SerializedData.Binary(encoded)
    }

    override fun decode(type: KType, data: SerializedData): Any? {
        return when(data){
            is SerializedData.Json   -> throw IllegalArgumentException("Unsupported type")
            is SerializedData.Text   -> throw IllegalArgumentException("Unsupported type")
            is SerializedData.Binary -> protobuf.decodeFromByteArray(module.serializer(type), data.value)
        }
    }
}

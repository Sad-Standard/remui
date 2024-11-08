/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalSerializationApi::class)

package org.remui.components.common

import org.remui.ClassModule
import org.remui.RemuiConfig
import org.remui.SerializedKType
import org.remui.decode
import org.remui.encode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
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
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

private val functionClasses = arrayOf(CFunction::class, CFunction1::class, CFunction2::class, CFunction3::class)

val CommonConfig = RemuiConfig(
    objectModule = CommonComponents + ClassModule(
        DynamicValue::class,
        Computable::class, Computable.Internal::class,
        *functionClasses
    ) + CBaseFunctions,
    serializerModule = { remui ->
        val objectPool = remui.objectPool

        val skt = typeOf<SerializedKType>()

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

//        polymorphicDefaultSerializer  (DynamicValue::class) { value -> dv }
//        polymorphicDefaultDeserializer(DynamicValue::class) { value -> dv }
        contextual(DynamicValue::class) { value -> dv }

        val cf = object: KSerializer<CFunction<*>> {
            override val descriptor: SerialDescriptor get() = PrimitiveSerialDescriptor("cf", PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: CFunction<*>) = encoder.encodeInt(objectPool[functionCategory, value])

            override fun deserialize(decoder: Decoder): CFunction<*> = objectPool[functionCategory, decoder.decodeInt()]
        }

        functionClasses.forEach { klass ->
            polymorphicDefaultSerializer<CFunction<Any?>>(klass as KClass<CFunction<Any?>>) { value -> cf }
            polymorphicDefaultDeserializer(klass as KClass<CFunction<*>>) { value -> cf }
        }
    }
)
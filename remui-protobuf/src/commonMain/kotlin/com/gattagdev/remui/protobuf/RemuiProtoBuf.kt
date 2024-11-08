/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalSerializationApi::class)

package com.gattagdev.remui.protobuf

import com.gattagdev.remui.RemuiConfig
import com.gattagdev.remui.RemuiSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KType


val ProtoBufConfig = RemuiConfig(
    remuiSerializer = ::RemuiProtoBufSerializer
)

class RemuiProtoBufSerializer(override val module: SerializersModule): RemuiSerializer {

    private val protobuf = kotlinx.serialization.protobuf.ProtoBuf {
        serializersModule = module
    }

    override fun encode(type: KType, value: Any?): ByteArray {
        val encoded =  protobuf.encodeToByteArray(module.serializer(type), value)
        return encoded
    }

    override fun decode(type: KType, data: ByteArray): Any? {
        return protobuf.decodeFromByteArray(module.serializer(type), data)
    }
}
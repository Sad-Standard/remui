/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui.json

import com.gattagdev.remui.RemuiConfig
import com.gattagdev.remui.RemuiSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KType

val JsonConfig = RemuiConfig(
    remuiSerializer = ::RemuiJsonSerializer
)

class RemuiJsonSerializer(override val module: SerializersModule): RemuiSerializer {

    private val json = Json {
        serializersModule = module
        useArrayPolymorphism = true
    }

    override fun encode(type: KType, value: Any?): ByteArray {
        val encoded =  json.encodeToString(module.serializer(type), value)
        return encoded.encodeToByteArray()
    }

    override fun decode(type: KType, data: ByteArray): Any? {
        val s = module.serializer(type)
        return json.decodeFromString(s, data.decodeToString())
    }
}
/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalSerializationApi::class)

package com.gattagdev.remui

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.protobuf.ProtoOneOf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

@Serializable
data class ServerUpdate(
    val interactionNr: Int,
    val addDiffs: Set<AddDiff>,
    val propDiffs: Set<PropDiff>,
    val removeDiffs: Set<RemoveDiff>,
    val rootId: Long
)

@Serializable
data class AddDiff(
    val id: Long,
    val classId: Int,
    val types: Map<String, SerializedKType>
)

@Serializable
data class PropDiff(
    val id: Long,
    val propId: PropId,
    val newValue: SerializedData
)

@Serializable
data class RemoveDiff(
    val id: Long
)

@Serializable
data class InteractionBlockMessage(
    val interactions: List<InteractionMessage>
)

@Serializable
data class InteractionMessage(
    val interactionNr: Int,
    val subjectId: Long,
    val name: String,
    val args: List<SerializedData>
)


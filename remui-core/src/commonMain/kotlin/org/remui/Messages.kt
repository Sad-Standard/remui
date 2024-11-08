/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalSerializationApi::class)

package org.remui

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

//private typealias KClassSerializable = @Serializable @Polymorphic KClass<Any>



/**
 * TODO Document
 */
@Serializable
data class ServerUpdate(
    val interactionNr: Int?             = null,
    val addDiffs     : List<AddDiff>    = emptyList(),
    val propDiffs    : List<PropDiff>   = emptyList(),
    val removeDiffs  : List<RemoveDiff> = emptyList(),
    val rootId       : Int?            = null
)

/**
 * TODO Document
 */
@Serializable
data class AddDiff(
    val id     : Int,
    val classId: Int,
    val types  : Map<String, SerializedKType> = mapOf(),
)

/**
 * TODO Document
 */
@Serializable
data class PropDiff(
    val id      : Int,
    val propId  : Int,
    val newValue: ByteArray
)

/**
 * TODO Document
 */
@Serializable
data class RemoveDiff(
    val id: Int
)

/**
 * TODO Document
 */
@Serializable
data class InteractionBlockMessage(
    val interactions: List<InteractionMessage>
)

/**
 * TODO Document
 */
@Serializable
data class InteractionMessage(
    val interactionNr: Int,
    val subjectId: Int,
    val name: String,
    val args: List<ByteArray>
)


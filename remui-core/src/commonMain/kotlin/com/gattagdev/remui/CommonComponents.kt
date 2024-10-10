/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalJsExport::class)
@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package com.gattagdev.remui

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.reflect.KType






val DomComponents = ComponentSet(
    listOf(
        DomNode::class,
        DomChildNode::class,
        DomParentNode::class,
    ),
    listOf(
        DomNode.Impl::class,
        DomElementNode::class,
        DomTextNode::class,
        DomEventListener::class,
        RemoteValue::class,
    )
)

sealed interface DomNode: RemuiStruct {
    sealed class Impl: RemuiStruct.Impl(), DomNode
}

sealed interface DomChildNode: RemuiStruct {

}

class RemoteValue<T>: RemuiStruct.Impl(){
    val remValue: T by _prop()
}

sealed interface DomParentNode: RemuiStruct {
    val childNodes: List<DomChildNode>
}

class DomElementNode: DomNode.Impl(), DomChildNode, DomParentNode {
    val tagName: String by _prop()
    val attributes: Map<String, String> by _prop()
    val eventListeners: List<DomEventListener> by _prop()
    override val childNodes: List<DomChildNode> by _prop()
}

class DomTextNode: DomNode.Impl(), DomChildNode {
    val wholeText: @Contextual Computable<@Contextual String> by _prop()
}

class DomEventListener: RemuiStruct.Impl() {
    val key: String by _prop()
    fun onEvent(): InteractionHandle = ::onEvent._call()
}




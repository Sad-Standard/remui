/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.components.html

import org.remui.ComponentSet
import org.remui.InteractionHandle
import org.remui.RStruct
import org.remui.components.common.RV
import kotlinx.serialization.Serializable

val DomComponents = ComponentSet(
    listOf(
        DomNode::class,
        DomChildNode::class,
        DomParentNode::class,
        DomBaseElementNode::class,
    ),
    listOf(
        DomNode.Impl::class,
        DomTagElementNode::class,
        DomTextNode::class,
        DomEventListener::class,
        DomInputNode::class,
        LazyDomNode::class
    )
)

sealed interface DomNode: RStruct {
    sealed class Impl: RStruct.Impl(), DomNode
}

sealed interface DomChildNode: RStruct

sealed interface DomParentNode: RStruct {
    val childNodes: List<DomChildNode>
}

class DomTextNode: DomNode.Impl(), DomChildNode {
    val wholeText: RV<String> by _prop()
}

sealed class DomBaseElementNode: DomNode.Impl(), DomChildNode {
    val attributes: Map<String, RV<String>> by _prop { mapOf() }
}

class DomTagElementNode: DomBaseElementNode(), DomParentNode {
             val tagName       : String                  by _prop()
             val eventListeners: List<DomEventListener>  by _prop { listOf() }
    override val childNodes    : List<DomChildNode>      by _prop { listOf() }
}

class DomEventListener: RStruct.Impl() {

    val key: String by _prop()

    fun onEvent(event: Event): InteractionHandle =
        ::onEvent._call(_arg(event))

}

class DomInputNode: DomBaseElementNode() {

    val value: RV<String> by _prop()

    fun onInput(old: String, new: String): InteractionHandle =
        ::onInput._call(_arg(old), _arg(new)) { ::value setTo RV(new) }

}

class LazyDomNode: DomNode.Impl(), DomChildNode, DomParentNode {

    val seen      by _prop { false }

    val showDelay by _prop { 0 }

    val hideDelay by _prop { -1 }

    override val childNodes by _prop { listOf<DomChildNode>() }

    fun onSeen(value: Boolean): InteractionHandle =
        if(seen == value) object: InteractionHandle { }
        else ::onSeen._call(_arg(value)) { ::seen setTo value }

}


@Serializable
//@Polymorphic
sealed interface Event {

    @Serializable
    data class Mouse (
        val altKey  : Boolean,
        val button  : Short,
//        val buttons : Short,
//        val clientX : Int,
//        val clientY : Int,
        val ctrlKey : Boolean,
        val metaKey : Boolean,
        val shiftKey: Boolean,
    ): Event

    @Serializable
    data class Input (
        val data: String,
    ): Event

    @Serializable
    data class Other(
        val type: String,
    ): Event

}

val DomEventListener_onEvent = DomEventListener::onEvent

val DomInputNode_onInput = DomInputNode::onInput

val LazyDomNode_onSeen = LazyDomNode::onSeen

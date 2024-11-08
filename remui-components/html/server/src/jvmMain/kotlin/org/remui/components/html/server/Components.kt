/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.components.html.server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Updater
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.remui.RStruct
import org.remui.components.common.Computable
import org.remui.components.common.RV
import org.remui.components.common.RV.Companion.invoke
import org.remui.components.html.DomBaseElementNode
import org.remui.components.html.DomChildNode
import org.remui.components.html.DomTagElementNode
import org.remui.components.html.DomEventListener
import org.remui.components.html.DomEventListener_onEvent
import org.remui.components.html.DomInputNode
import org.remui.components.html.DomInputNode_onInput
import org.remui.components.html.DomTextNode
import org.remui.components.html.Event
import org.remui.components.html.LazyDomNode
import org.remui.components.html.LazyDomNode_onSeen
import org.remui.server.RemoteComponent
import org.remui.server.RemuiMutableContext
import org.remui.server.RemuiNode
import org.remui.server.mutableContext
import kotlin.collections.set

interface AttrSpec {
    infix fun String.setTo(value: RV<String>)
    infix fun String.`=`  (value: RV<String>) = this setTo value
    infix fun String.setTo(value: String) = this setTo RV(value)
    infix fun String.`=`  (value: String) = this setTo value
    infix fun String.setTo(value: Computable<String>) = this setTo RV(computable = value)
    infix fun String.`=`  (value: Computable<String>) = this setTo value
}

interface HtmlElementSpec {
    @Composable
    operator fun String.unaryPlus()

    @Composable
    operator fun Computable<String>.unaryPlus()

    @Composable
    infix fun String.then(value: Event.() -> Unit)

    @Composable
    fun onClick(handler: Event.() -> Unit) {
        "click" then {
            handler(this)
        }
    }

    @Composable
    fun onInput(handler: Event.Input.() -> Unit) {
        "input" then {
            handler(this as Event.Input)
        }
    }
}

context(Updater<RemuiNode.Struct<T>>, RemuiMutableContext)
fun <T: DomBaseElementNode> applyAttributes(attrs: AttrSpec.() -> Unit) {
    val map = mutableMapOf<String, RV<String>>()
    object : AttrSpec {
        override infix fun String.setTo(value: RV<String>) {
            map[this] = value
        }
    }.attrs()
    set(map) { struct::attributes setTo it }
}

@Composable
fun HTMLElement(
    tagName: String,
    attrs: AttrSpec.() -> Unit = { },
    content: (@Composable HtmlElementSpec.() -> Unit) = { },
) {
    RemoteComponent<DomTagElementNode>(update = {
        with(mutableContext) {
            set(tagName) { struct::tagName setTo it }
            applyAttributes(attrs)
        }
    }) {

        component<RStruct>(
            update = {
                ::childNodes     setTo it.filterIsInstance<DomChildNode    >()
                ::eventListeners setTo it.filterIsInstance<DomEventListener>()
            },
            content = { htmlElementContent(content) }
        )
    }
}

@Composable
private inline fun htmlElementContent(content: (@Composable HtmlElementSpec.() -> Unit) ){
    object : HtmlElementSpec {

        @Composable
        override fun String.unaryPlus() {
            HTMLText(RV(this))
        }

        @Composable
        override fun Computable<String>.unaryPlus() {
            HTMLText(RV(computable = this))
        }

        @Composable
        override infix fun String.then(handler: Event.() -> Unit) {
            eventListener(this, handler)
        }

    }.content()
}

@Composable
private fun eventListener(key: String, handler: Event.() -> Unit){
    RemoteComponent<DomEventListener>(
        update = {
            set(key) { struct::key setTo it }
            set(handler) { h ->
                struct.handle(DomEventListener_onEvent) { e -> h(e) }
            }
        },
    ) { }



}


@Composable
fun div(
    attrs  : AttrSpec.() -> Unit = { },
    content: @Composable HtmlElementSpec.() -> Unit
) {
    HTMLElement("div",
        attrs = attrs,
        content = content
    )
}

@Composable
fun span(
    attrs  : AttrSpec.() -> Unit = { },
    content: @Composable HtmlElementSpec.() -> Unit
) {
    HTMLElement("span",
        attrs = attrs,
        content = content
    )
}

@Composable
fun h1(
    attrs  : AttrSpec.() -> Unit = { },
    content: @Composable HtmlElementSpec.() -> Unit
) {
    HTMLElement("h1",
        attrs = attrs,
        content = content
    )
}

@Composable
fun h2(
    attrs  : AttrSpec.() -> Unit = { },
    content: @Composable HtmlElementSpec.() -> Unit
) {
    HTMLElement("h2",
        attrs = attrs,
        content = content
    )
}

@Composable
fun h3(
    attrs  : AttrSpec.() -> Unit = { },
    content: @Composable HtmlElementSpec.() -> Unit
) {
    HTMLElement("h3",
        attrs = attrs,
        content = content
    )
}

@Composable
fun h4(
    attrs  : AttrSpec.() -> Unit = { },
    content: @Composable HtmlElementSpec.() -> Unit
) {
    HTMLElement("h4",
        attrs = attrs,
        content = content
    )
}

@Composable
fun h5(
    attrs  : AttrSpec.() -> Unit = { },
    content: @Composable HtmlElementSpec.() -> Unit
) {
    HTMLElement("h5",
        attrs = attrs,
        content = content
    )
}

@Composable
fun h6(
    attrs  : AttrSpec.() -> Unit = { },
    content: @Composable HtmlElementSpec.() -> Unit
) {
    HTMLElement("h6",
        attrs = attrs,
        content = content
    )
}

@Composable
fun p(
    attrs: AttrSpec.() -> Unit = { },
    content: @Composable HtmlElementSpec.() -> Unit,
) {
    HTMLElement("p",
        attrs = attrs,
        content = content
    )
}


@Composable
fun input(
    value  : RV<String>,
    attrs: AttrSpec.() -> Unit = { },
    onInput: (old: String, new: String) -> Unit = { _, _ -> },
) {
    RemoteComponent<DomInputNode>(
        update = {
            set(value) { struct::value setTo it }
            set(onInput) {
                struct.handle(DomInputNode_onInput) { o, n -> onInput(o, n) }
            }
            applyAttributes(attrs)
        }
    ) { }
}

@Composable
fun lazyDiv(content: @Composable HtmlElementSpec.() -> Unit) {

    val seenState = remember { mutableStateOf(false) }

    RemoteComponent<LazyDomNode>(
        update = {
            set(seenState.value) { struct::seen setTo it }
            init {
                struct.handle(LazyDomNode_onSeen) {  seenState.value = it }
            }
        }
    ) {
        component<RStruct>(
            update = {
                ::childNodes setTo it.filterIsInstance<DomChildNode>()
            },
            content = {
                if(seenState.value) htmlElementContent(content)
                else p { +"Loading..." }
            }
        )

    }
}

@Composable
fun HTMLText(text: RV<String>) {
    RemoteComponent<DomTextNode>(update = {
        set(text) { struct::wholeText setTo it }
    }) { }
}
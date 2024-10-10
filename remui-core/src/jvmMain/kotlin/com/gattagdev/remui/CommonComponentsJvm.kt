/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.ReusableComposeNode
import androidx.compose.runtime.State
import androidx.compose.runtime.Updater
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@Composable
inline fun <reified T> Remote(state: State<T>): Computable<T>{
    val remuiStructContext = LocalRemuiStructContext.current

    val node = remember { RemuiNode.Struct<RemoteValue<T>>(remuiStructContext.construct()) }
    ComposeNode<RemuiNode.Struct<RemoteValue<T>>, RemuiApplier>(
        factory = { node },
        update  = {
            with(mutableContext) {
                set(state.value) { struct::remValue setTo it }
            }
        },
        content = {  }
    )
    return Computable<T>(Computable.Remote(node.struct as RemoteValue<Nothing>))
}

@Composable
inline fun <reified T> Remote(value: () -> T): Computable<T>{
    val remuiStructContext = LocalRemuiStructContext.current

    val node = remember { RemuiNode.Struct<RemoteValue<T>>(remuiStructContext.construct()) }
    val c = value()
    ComposeNode<RemuiNode.Struct<RemoteValue<T>>, RemuiApplier>(
        factory = { node },
        update  = {
            with(mutableContext) {
                set(c) { struct::remValue setTo it }
            }
        },
        content = {  }
    )
    return Computable<T>(Computable.Remote(node.struct as RemoteValue<Nothing>))
}


interface AttrSpec {
    infix fun String.setTo(value: String)
}

interface HtmlElementSpec {
    @Composable
    operator fun String.unaryPlus()

    @Composable
    operator fun Computable<String>.unaryPlus()

    @Composable
    infix fun String.then(value: () -> Unit)

    @Composable
    fun onClick(handler: () -> Unit) {
        "click" then handler
    }
}

@Composable
fun HTMLElement(
    tagName: String,
    attrs: AttrSpec.() -> Unit = { },
    content: (@Composable HtmlElementSpec.() -> Unit) = { },
) {
    RemoteComponent<DomElementNode>(update = {
        set(tagName) { struct::tagName setTo it }
        val map = mutableMapOf<String, String>()
        object : AttrSpec {
            override infix fun String.setTo(value: String) { map[this] = value }
        }.attrs()
        set(map) { struct::attributes setTo it }
    }) {

        component<RemuiStruct>(
            update = {
                ::childNodes setTo it.filterIsInstance<DomChildNode>()
                ::eventListeners setTo it.filterIsInstance<DomEventListener>()
            },
            content = {
                object : HtmlElementSpec {

                    @Composable
                    override fun String.unaryPlus() {
                        +this.rc
                    }


                    @Composable
                    override fun Computable<String>.unaryPlus() {
                        HTMLText(this)
                    }

                    @Composable
                    override infix fun String.then(handler: () -> Unit) {
                        val key = this
                        RemoteComponent<DomEventListener>(
                            update = {
                                set(key) { struct::key setTo it }
                                set(handler) { struct::onEvent setTo {
                                    handler()
                                    InteractionHandle()
                                } }
                            },
                        ) {}
                    }
                }.content()
            }
        )
    }
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
fun HTMLText(text: Computable<String>) {
    RemoteComponent<DomTextNode>(update = {
        set(text) { struct::wholeText setTo it }
    }) { }
}

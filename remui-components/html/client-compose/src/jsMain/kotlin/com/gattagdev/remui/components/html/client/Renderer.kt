/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui.components.html.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.web.events.SyntheticEvent
import com.gattagdev.remui.Remui
import com.gattagdev.remui.components.common.ComputableContext
import com.gattagdev.remui.components.html.DomInputNode
import com.gattagdev.remui.components.html.DomTagElementNode
import com.gattagdev.remui.components.html.DomNode
import com.gattagdev.remui.components.html.DomTextNode
import com.gattagdev.remui.components.html.Event
import com.gattagdev.remui.components.html.LazyDomNode
import com.gattagdev.remui.getId
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextInput
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement



@Composable
fun Render(remui: Remui){
    remui.root?.let { DomNodeRender(it as DomNode) }
}

@Composable
fun RootWrapper(body: @Composable () -> Unit) {
    Div {
        body()
    }
}


@Composable
fun DomNodeRender(element: DomNode){
    key(getId(element).id){
        with(element) { when(this) {
            is DomTagElementNode -> Renderer()
            is DomInputNode      -> Renderer()
            is DomTextNode       -> Renderer()
            is LazyDomNode       -> Renderer()
        } }
    }
}


@Composable
fun DomTagElementNode.Renderer(){
    TagElement<HTMLElement>(
        tagName = tagName,
        applyAttrs = {
            attributes.forEach { (k, v) ->
                attr(k, v.value)
            }
            eventListeners.forEach { el ->
                addEventListener<SyntheticEvent<HTMLElement>>(el.key) { sEvent ->
                    el.onEvent(when(val n = sEvent.nativeEvent) {
                        is org.w3c.dom.events.InputEvent -> Event.Input((sEvent.target as HTMLInputElement).value)
                        else -> Event.Other(sEvent.nativeEvent.type)
                    })
                }
            }
        }
    ) {
        childNodes.forEach {
//            println("Rendering Child")
            DomNodeRender(it as DomNode)
        }
    }
}

@Composable
fun DomInputNode.Renderer(){
    TextInput(with(object: ComputableContext{}) { value.value }){
        attributes.forEach { (key, value) ->
            attr(key, value.value)
        }
        onInput { event ->
            onInput(event.target.value, event.value)
        }
    }
}

@Composable
fun DomTextNode.Renderer(){
    Text(with(object: ComputableContext{}){
        wholeText.value
    })
}

@Composable
fun LazyDomNode.Renderer() {
    Div(
        attrs = {
            ref { element ->

                registerObserver(element) { onSeen(it) }

                object: DisposableEffectResult {
                    override fun dispose() { }
                }
            }
        }
    ) {
        childNodes.forEach { DomNodeRender(it as DomNode) }
    }
}

private fun registerObserver(element: HTMLDivElement, onSeen: (Boolean) -> Unit) {
    lateinit var io: IntersectionObserver

    val options = object : IntersectionObserverInit {
        override var rootMargin: String? = "50px 50px 50px 50px"  // Expands by 50% on all sides
    }

    io = IntersectionObserver({ entries, _ ->
        entries.forEach { e ->
            onSeen(e.isIntersecting)
        }
    }, options)

    io.observe(element)

}

external class IntersectionObserver(
    callback: (Array<IntersectionObserverEntry>, IntersectionObserver) -> Unit,
    options: IntersectionObserverInit = definedExternally
) {

    fun observe(target: Element)

    fun unobserve(target: Element)

    fun disconnect()

}

external interface IntersectionObserverEntry {

    val isIntersecting: Boolean

    val target: Element

}

external interface IntersectionObserverInit {

    var root: Element?
        get() = definedExternally
        set(value) = definedExternally

    var rootMargin: String?
        get() = definedExternally
        set(value) = definedExternally

    var threshold: dynamic /* Double | Array<Double> */
        get() = definedExternally
        set(value) = definedExternally
}


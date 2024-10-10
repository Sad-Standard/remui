/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalSerializationApi::class)

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticEvent
import com.gattagdev.remui.ComputableContext

import com.gattagdev.remui.DomComponents
import com.gattagdev.remui.DomElementNode
import com.gattagdev.remui.DomNode
import com.gattagdev.remui.DomTextNode
import com.gattagdev.remui.RemuiContext
import com.gattagdev.remui.RemuiJsonSerializer
import com.gattagdev.remui.RemuiProtobufSerializer
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement


@OptIn(DelicateCoroutinesApi::class)
fun main() {


    GlobalScope.launch {
        val httpClient = HttpClient{ install(WebSockets) }

        val context = RemuiContext(
            DomComponents,
            httpClient.webSocketSession("ws://localhost:5566/remui"),
            serializerBuilder = ::RemuiProtobufSerializer
        )

        renderComposable(rootElementId = "root") {
            val root = context.root
            if(root != null) {
                println(root::class)
                DomNodeRender(root as DomNode)
            }

        }
    }
}

@Composable
fun DOMScope<Element>.DomNodeRender(element: DomNode){
    with(element) { when(this) {
        is DomElementNode -> TagElement<Element>(
            tagName = tagName,
            applyAttrs = {

                attributes.forEach { (key, value) -> attr(key, value) }
                eventListeners.forEach { el ->
                    addEventListener<SyntheticEvent<HTMLElement>>(el.key) {
                        el.onEvent()
                    }
                }
            }
        ) {
            childNodes.forEach { DomNodeRender(it as DomNode) }
        }
        is DomTextNode -> Text(with(object: ComputableContext{}){
            wholeText.value
        })
    } }

}
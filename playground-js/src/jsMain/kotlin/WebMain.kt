/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(DelicateCoroutinesApi::class)

import org.remui.client.RemuiClient
import org.remui.client.RemuiClient.Companion.invoke
import org.remui.components.html.client.Render
import org.remui.ktor.createRemuiIO
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import my.test.MyConfig
import org.jetbrains.compose.web.renderComposable


fun main() {

    GlobalScope.launch {
        val httpClient = HttpClient { install(WebSockets) }
//        val host = "10.5.6.108"
        val host = "localhost"
        val ws = httpClient.webSocketSession("ws://$host:5566/remui")

        with(ws){
            try {
                val context = RemuiClient(MyConfig, createRemuiIO())

                renderComposable("root") {
                    println("Hello")
                    Render(context)
                }

            } catch (t: Throwable){
                t.printStackTrace()
                throw t
            }

        }

    }

}


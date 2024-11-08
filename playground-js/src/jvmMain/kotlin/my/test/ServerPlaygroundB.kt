/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package my.test

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import org.remui.components.common.server.Remote
import org.remui.components.html.server.div
import org.remui.components.html.server.h1
import org.remui.components.html.server.p
import org.remui.ktor.createRemuiIO
import org.remui.server.RemuiServer
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket


fun main() {

    embeddedServer(Netty, port = 5566) {
        install(WebSockets)
        routing {
            get("/") { call.respondText("") }
            webSocket("/remui") {
                RemuiServer(MyConfig, createRemuiIO()){
                    div {
                        h1 { +"This is my test app" }
                        var count by remember { mutableStateOf(0) }
                        val remote = Remote { count }

                        p {
                            +"The count is $count"
//                            +remote.asString()
                            onClick { count++ }
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}
/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package my.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.remui.components.common.server.Remote
import org.remui.components.common.asString
import org.remui.components.html.server.*
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
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.sin


@OptIn(InternalComposeApi::class)
fun main(){
    println("TestMain::my.test.main is running")

    embeddedServer(Netty, port = 5566) {
        install(WebSockets)
        routing {
            get("/") { call.respondText("") }
            webSocket("/remui") {
                try {
                    RemuiServer(MyConfig, createRemuiIO()) {
                        enableLiveLiterals()
                        TestComposable()
                    }
                }catch (ex: Throwable){
                    ex.printStackTrace()
                    throw ex
                }
            }
        }
    }.start(wait = true)
}

@Composable
fun TestComposable(){

    val s = remember { mutableStateOf(0) }
    val time = remember { mutableStateOf(0L) }
    var count by remember { s }
    LaunchedEffect(Unit) {
        val start = System.currentTimeMillis()
        while (true) {
            delay(17)
            count++
            time.value = System.currentTimeMillis() - start
        }
    }
    div {
        TestSubComposable(s, time)
    }
}
//}

@Composable
@Preview
fun TestSubComposable(state: State<Int>, time: State<Long>){

    val clicks = remember { mutableStateOf(0) }

    val rem = Remote { clicks.value }
    val myTime = Remote { time.value }
    div {
        HTMLElement("h${((state.value / 10) % 5) + 1}", {
            val r = (sin(state.value / (Math.PI * 2)) + 1.0) * 127.5
            "style" setTo "color: rgb($r, 0, 0)"
        }) {
            +"This is my app"
        }
        p {
            onClick { clicks.value++ }
            +"Number of clicks: "
            +rem.asString()
        }
        p {
            +"State: "
            +rem.asString()
        }
        p {
            +"Time: "
            +myTime.asString()
        }
        div {
            val size = 10
            repeat(clicks.value % size) {
                p {
                    +"Number "
                    +"${it + clicks.value % size}"
                }
            }
        }
    }
}
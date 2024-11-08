/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui.ktor

import com.gattagdev.remui.RemuiIO
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch


/**
 * TODO Document
 */
fun DefaultWebSocketSession.createRemuiIO(useText: Boolean = false): RemuiIO {

    val send    = Channel<ByteArray>(UNLIMITED)
    val receive = Channel<ByteArray>(UNLIMITED)

    launch {
        incoming.consumeEach { frame ->
            when(frame) {
                is Frame.Text   -> receive.send(frame.readText().encodeToByteArray())
                is Frame.Binary -> receive.send(frame.data)
                else            -> { }
            }
        }
    }

    launch {
        send.consumeEach { data -> send(data) }
    }

    return RemuiIO(send, receive)
}
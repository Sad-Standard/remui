/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

import androidx.compose.runtime.*
import com.gattagdev.remui.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.core.toByteArray
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.concurrent.Executors
import kotlin.math.sin


fun main(){
    println("TestMain::main is running")

    embeddedServer(Netty, port = 5566) {
        install(WebSockets)
        install(CORS) {
            anyHost()  // Accepts requests from any origin
            allowHeader(HttpHeaders.ContentType)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
        }
        routing {
            get("/") { call.respondText("", ContentType.Text.Plain) }
            webSocket("/remui") {
                try {
                    val executor = Executors.newSingleThreadScheduledExecutor()
                    lateinit var recomposer: Recomposer

                    withContext(executor.asCoroutineDispatcher() + YieldFrameClock) {
                        GlobalSnapshotManager.ensureStarted()
                        recomposer = Recomposer(coroutineContext)
                        launch { recomposer.runRecomposeAndApplyChanges() }
                        val remuiStructContext = RemuiStructContext(
                            DomComponents,
                            serializerBuilder = ::RemuiProtobufSerializer
                        )
                        val myApplier   = RemuiApplier(remuiStructContext)
                        val composition = Composition(myApplier, recomposer)
                        launch {
                            try {
                                composition.setContent {
                                    CompositionLocalProvider(
                                        LocalRemuiStructContext provides remuiStructContext
                                    ) {
                                        TestComposable()
                                    }
                                }
//                        recomposer.close()
                                recomposer.join()
                            } finally {
                                composition.dispose()
                            }
                        }
                        launch {
                            for (frame in incoming) {
                                if(frame !is Frame.Binary) continue
                                val data = (frame).data
                                val ibm = remuiStructContext.serializer.decode<InteractionBlockMessage>(SerializedData.Binary(data))
                                remuiStructContext.handleInteractionBlock(ibm)
                            }
                        }
                        launch {
                            try {
                                for (su in remuiStructContext.toSend) {
                                    send(when(val bin = remuiStructContext.serializer.encode(su)){
                                        is SerializedData.Json   -> bin.value.toString().toByteArray()
                                        is SerializedData.Text   -> bin.value.toByteArray()
                                        is SerializedData.Binary -> bin.value
                                    })
                                }
                            } catch (t: Throwable){
                                t.printStackTrace()
                                throw t
                            }
                        }.join()
                    }
                }catch (ex: Throwable){
                    ex.printStackTrace()
                    throw ex
                }
            }
        }
    }.start(wait = true)
//
//    println(ServerUpdate.javaClass.classLoader.loadClass("com.gattagdev.remui.generated.BaseElementImpl"))
//
//    runBlocking {
//        while (true){
//            delay(1000)
//        }
//
//
//    }
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
    TestSubComposable(s, time)
//    Test2(time)
}
//@Composable
//fun Test2(time: State<Long>){
//    val other = remember { mutableStateOf(null) }
//    val shared = Remote({
//
//    })
//    div {
//        p {
//            +(-Remote(time) * -1L.rc).asString()
//        }
//    }
//}

@Composable
fun TestSubComposable(state: State<Int>, time: State<Long>){

    val clicks = remember { mutableStateOf(0) }
    div {
        HTMLElement("h${((state.value / 10) % 5) + 1}", {
            val r = (sin(state.value / (Math.PI * 2)) + 1.0) * 127.5
            "style" setTo "color: rgb($r, 0, 0)"
        }) {
            +"This is my app"
        }
        p {
            onClick { clicks.value++ }
            +"Number of clicks: ${clicks.value}"
        }
        p {
            +"State: "
            +"${clicks.value}"
        }
        p {
            +"Time: "
            +"${time.value}"
        }
        div {
            val size = 100
            repeat(clicks.value % size) {
                p {
                    +"Number "
                    +"${it + clicks.value % size}"
                }
            }
        }
    }

//    println(text.text)

}

private object YieldFrameClock : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(
        onFrame: (frameTimeNanos: Long) -> R
    ): R {
        yield()
        return onFrame(System.nanoTime())
    }
}

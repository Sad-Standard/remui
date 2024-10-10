/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalJsReflectionCreateInstance::class, ExperimentalSerializationApi::class)

package com.gattagdev.remui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.plugins.websocket.ClientWebSocketSession
import io.ktor.utils.io.core.toByteArray
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.createInstance


class RemuiContext private constructor(
    baseObjectModule: ObjectModule,
    serializerBuilder: (SerializersModule) -> RemuiSerializer,
    val sendInteraction: RemuiContext.(InteractionBlock) -> Unit
)
{
    val objectPool = ObjectPool((baseObjectModule + BaseClassModule + RemuiClassModule))

    private val structs         = mutableMapOf<Long, RemuiStruct.Impl>()
    private var nextInteraction = 1
    @PublishedApi
    internal var activeBlock    = null as (InteractionBlock?)
//    val toSend                  = Channel<InteractionBlock>(UNLIMITED)
    val awaitingProcessing      = mutableListOf<InteractionRecord>()
    private val _rootStruct     = mutableStateOf<RemuiStruct.Impl?>(null)

    val root: RemuiStruct? get() = _rootStruct.value

//    val serializerModule = buildSerializer(
//        structInterfaces + structTypeList,
//        { (it as RemuiStruct.Impl).internal.id.id },
//        { structs[it]!! }
//    )
    val serializer = serializerBuilder(
        SerializersModule {
//            include(Computable.module)
            include(buildSerializer(
                objectPool,
                { (it as RemuiStruct.Impl).internal.id.id },
                { structs[it]!! }
            ))
        }
    )

    companion object {
        context(CoroutineScope)
        operator fun invoke(
            baseObjectModule: ObjectModule,
            webSocket: ClientWebSocketSession,
            serializerBuilder: (SerializersModule) -> RemuiSerializer,
        ): RemuiContext {
            val context = RemuiContext(
                baseObjectModule,
                serializerBuilder
            ) { block ->

                val message = InteractionBlockMessage(
                    interactions = block.interactions.map { interaction ->
                        InteractionMessage(
                            interactionNr = interaction.interactionNr,
                            subjectId     = interaction.struct.internal.id.id,
                            name          = interaction.interactionName,
                            args          = interaction.args.map { serializer.encode(it.type, it.value) }
                        )
                    }
                )

                launch {
                    webSocket.send(when(val sd = serializer.encode(message)){
                        is SerializedData.Json   -> sd.value.toString().toByteArray()
                        is SerializedData.Text   -> sd.value.toByteArray()
                        is SerializedData.Binary -> sd.value
                    })
                }

            }

            launch {
                for (message in webSocket.incoming){
                    if(message is Frame.Binary) {
                        try {
                            context.processUpdate(context.serializer.decode<ServerUpdate>(SerializedData.Binary(message.data)))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            throw e
                        }

                    }
                }
            }

            return context
        }
    }


    inner class InteractionBlock {
        val interactions = mutableListOf<InteractionRecord>()
    }

    suspend fun processUpdate(serverUpdate: ServerUpdate) {
        val nr = serverUpdate.interactionNr

        // Process AddDiffs
        serverUpdate.addDiffs.forEach { update ->
            val oldCC = currentConstructionContext
            try {
                val id = RemuiId(this, update.id)
                currentConstructionContext = ConstructionContext(id, update.types.map { it -> it.key to objectPool.decode(it.value) }.toMap())
                println(update.classId)
                val new = (objectPool[classKey, update.classId]).createInstance() as RemuiStruct.Impl
                structs[update.id] = new
            } finally {
                currentConstructionContext = oldCC
            }
        }

        // Process PropDiffs
        serverUpdate.propDiffs.forEach { update ->
            val struct = structs[update.id]?: error("Struct id (${update.id}) could not be found")
            val propState = struct.internal.propertiesById[update.propId] ?: error("Property (${update.propId}) could not be found for struct (${update.id}) with type (${struct::class.simpleName})")
            val newValue = serializer.decode(propState.type, update.newValue)
            propState.truth = newValue
            propState.trySyncTruth(nr)
        }

        // Process RemoveDiffs
        serverUpdate.removeDiffs.forEach {
            structs.remove(it.id)
        }

        // Process root
        _rootStruct.value = structs[serverUpdate.rootId] ?: error("Root struct (${serverUpdate.rootId}) could not be found")

        // Sync truth
        while (true) {
            val next = awaitingProcessing.firstOrNull()
            if(next == null || next.interactionNr > nr) break
            awaitingProcessing.removeFirst()
            next.markProcessed()
        }
    }

    fun <R> openInteractionBlock(body: () -> R): R {
        val oldBlock = activeBlock
        try {
            if(oldBlock == null) activeBlock = InteractionBlock()
            return body()
        } finally {
            val block = activeBlock
            if(oldBlock == null) sendInteraction(block!!)
            activeBlock = oldBlock
        }
    }

    fun handleInteraction(internal: RSInternal, name: String, args: Array<out InteractionArg<*>>): InteractionHandle {
        return openInteractionBlock {
            val record = InteractionRecord(
                context         = this,
                interactionNr   = nextInteraction++,
                struct          = internal.struct,
                interactionName = name,
                args            = args
            )
            awaitingProcessing += record
            activeBlock!!.interactions += record
            record.handle
        }
    }
}

class InteractionRecord(
    val context        : RemuiContext,
    val interactionNr  : Int,
    val struct         : RemuiStruct.Impl,
    val interactionName: String,
    val args           : Array<out InteractionArg<*>>,
)
{
    val handle = InteractionHandle(this)

    internal var responseProcessed = false
    internal val processedSignal   = MutableSharedFlow<Unit>(replay = 1)
    internal val toSync            = mutableSetOf<PropertyState>()

    internal suspend fun markProcessed() {
        if(responseProcessed) return
        responseProcessed = true
        toSync.forEach { it.trySyncTruth(interactionNr) }
        processedSignal.emit(Unit)
    }

    private fun setLocalState(prop: PropertyState, value: Any?) {
        prop.lockSyncTill(interactionNr)
        prop.state.value = value
        toSync += prop
    }
}

actual class InteractionHandle(val record: InteractionRecord) {
    suspend fun join() {
        if(record.responseProcessed) return
        record.processedSignal.first()
    }
}

private val currentContext: RemuiContext? = null

private var currentConstructionContext: ConstructionContext? = null

class ConstructionContext(
    val id: RemuiId,
    val types: Map<String, KType>
)

actual fun initInternal(impl: RemuiStruct.Impl): RSInternal {
    val cc = currentConstructionContext!!
    return RSInternal(impl, cc.id, cc.types)
}

internal class PropertyState(
    val name  : String,
    val id    : PropId,
    val type  : KType,
    val state : MutableState<Any?>,
    var truth : Any?,
    var syncAt: Int,
) {

    fun trySyncTruth(interactionNr: Int) {
        if(interactionNr >= syncAt) state.value = truth
    }

    fun lockSyncTill(interactionNr: Int) {
        if(interactionNr > syncAt) syncAt = interactionNr
    }
}

actual class RSInternal(actual val struct: RemuiStruct.Impl, actual val id: RemuiId, actual val types: Map<String, KType>) {

    private var nextPropId = 0
    internal val propertiesByName = mutableMapOf<String, PropertyState>()
    internal val propertiesById = mutableMapOf<PropId, PropertyState>()

    actual fun <T> makePropertyDelegate(name: String, type: KType): ReadOnlyProperty<RemuiStruct, T> {
        val prop = PropertyState(
            name   = name,
            id     = nextPropId++,
            type   = type,
            state  = mutableStateOf(null),
            truth  = null,
            syncAt = 0
        ).also {
            propertiesByName[name] = it
            propertiesById[it.id] = it
        }

        return object: ReadOnlyProperty<RemuiStruct, T> {
            override fun getValue(thisRef: RemuiStruct, property: KProperty<*>): T = prop.state.value as T
        }
    }

    actual fun callInteraction(name: String, args: Array<out InteractionArg<*>>): InteractionHandle = id.context.handleInteraction(this, name, args)
}

actual data class RemuiId(val context: RemuiContext, val id: Long)
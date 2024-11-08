/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui.client


import com.gattagdev.remui.AddDiff
import com.gattagdev.remui.InteractionArg
import com.gattagdev.remui.InteractionBlockMessage
import com.gattagdev.remui.InteractionHandle
import com.gattagdev.remui.InteractionMessage
import com.gattagdev.remui.PersistenceManager
import com.gattagdev.remui.PredictorScope
import com.gattagdev.remui.PropDiff
import com.gattagdev.remui.RProperty
import com.gattagdev.remui.RPropertyDescriptor
import com.gattagdev.remui.RStruct
import com.gattagdev.remui.RemoveDiff
import com.gattagdev.remui.Remui
import com.gattagdev.remui.RemuiConfig
import com.gattagdev.remui.RemuiIO
import com.gattagdev.remui.ServerUpdate
import com.gattagdev.remui.access
import com.gattagdev.remui.classKey
import com.gattagdev.remui.decode
import com.gattagdev.remui.encode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.collections.contains
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.plusAssign
import kotlin.collections.removeFirst
import kotlin.collections.set
import kotlin.collections.toMap
import kotlin.printStackTrace
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty0
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.text.trimIndent
import kotlin.to

internal val Remui.client: RemuiClient get() = this as RemuiClient

class RemuiClient private constructor(config: RemuiConfig): Remui(config) {

    companion object {

        /**
         * TODO Document
         */
        context(CoroutineScope)
        operator fun invoke(config: RemuiConfig, io: RemuiIO): Remui {
            val remui = RemuiClient(config)

            launch {
                for (message in io.receive) {
                    try {
                        remui.processUpdate(remui.serializer.decode<ServerUpdate>(message))
                    } catch (e: Error) { // TODO remove me
                        e.printStackTrace()
                        throw e
                    }
                }
            }


            launch {
                for (message in remui.blocksToSend) {
                    io.send.send(remui.serializer.encode(message))
                }
            }

            return remui
        }
    }

    /**
     * TODO Document
     */
    private var nextInteraction = 1

    /**
     * TODO Document
     */
    private var latestResponseNr = -1

    /**
     * TODO Document
     */
    internal var activeBlock: InteractionBlock? = null

    /**
     * TODO Document
     */
    private val blocksToSend = Channel<InteractionBlockMessage>(Channel.Factory.UNLIMITED)

    /**
     * TODO Document
     */
    private val awaitingProcessing = mutableListOf<InteractionRecord>()

    /**
     * TODO Document
     */
    inner class InteractionBlock {

        /**
         * TODO Document
         */
        internal val interactions = mutableListOf<InteractionRecord>()

    }

    /**
     * TODO Document
     */
    private suspend fun processUpdate(update: ServerUpdate) = with(update) {
        try {
            interactionNr?.let { latestResponseNr = it }

            addDiffs.forEach(::processAddDiff)
            println(addDiffs.size)
            propDiffs.forEach { processPropDiff(it, latestResponseNr) }
            removeDiffs.forEach(::processRemoveDiff)
            rootId?.let { processRootId(it) }

            processAwaitingInteractions(latestResponseNr)
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }

    }

    /**
     * TODO Document
     */
    private fun processAddDiff(diff: AddDiff) {
        val kClass = objectPool[classKey, diff.classId] as KClass<RStruct.Impl> // TODO change from classKey to the struct component category on both sides
        val id = diff.id
        check(id !in _structs) {
            """
            |Update from server tried to add a struct with the same id ($id) as another struct. (class: $kClass)
            |This is most likely an internal bug in Remui, PLEASE REPORT TO REMUI MAINTAINERS!
        """.trimIndent()
        }

        val types = diff.types.map { it -> it.key to objectPool.decode(it.value) }.toMap()
        // TODO provide better error messages

        val struct = createStruct(kClass, id, types)

        _structs[id] = struct
    }

    /**
     * TODO Document
     */
    private fun processPropDiff(diff: PropDiff, nr: Int) {
        val struct = _structs[diff.id]?: error(
            """
            |The server tried to update a property of a struct that does not exist locally. (id: ${diff.id}, propIndex: ${diff.propId})
            |The UI running on the server is probably retaining a reference to a struct that it should not.
            |This should have been caught on the server, PLEASE REPORT TO REMUI MAINTAINERS!
        """.trimIndent()
        )

        val prop = access(struct).properties.getOrNull(diff.propId) as RPropertyClient<Any?>? ?: error(
            """
            |The server tried to update a property (propId: ${diff.propId}) that is not known for node. (id: ${diff.id}, class: ${struct::class}
            |Ensure your client and server ObjectPools are the same and that the struct classes deployed on the client and server have the same structure.
        """.trimIndent()
        )

        val value = serializer.decode(prop.type, diff.newValue)
        // Need to figure out what to do about errors here

        prop.setTruth(value)

        prop.trySync(nr)
    }

    /**
     * TODO Document
     */
    private fun processRemoveDiff(diff: RemoveDiff) {
        check(diff.id in _structs) {
            """
            |Server tried to remove node that never existed or no longer exists locally. (id: ${diff.id})
            |This is most likely an internal bug in the Remui server, PLEASE REPORT TO REMUI MAINTAINERS!
        """.trimIndent()
        }

        _structs.remove(diff.id)
    }

    /**
     * TODO Document
     */
    private fun processRootId(id: Int) {
        check(id in _structs) {
            """
            |Server tried to set the root node with an id that never existed or no longer exists locally. (id: ${id})
            |The UI running on the server is probably retaining a reference to a struct that it should not.
            |This should have been caught on the server, PLEASE REPORT TO REMUI MAINTAINERS!
        """.trimIndent()
        }

        persistenceAny.set(this, "root", typeOf<RStruct?>(), rootStore, _structs[id]!!)
    }

    /**
     * TODO Document
     */
    private suspend fun processAwaitingInteractions(nr: Int) {
        while (true) {
            val next = awaitingProcessing.firstOrNull()
            if(next == null || next.interactionNr > nr) break
            awaitingProcessing.removeFirst()
            next.markProcessed()
        }
    }

    /**
     * TODO Document
     */
    fun <R> openInteractionBlock(body: () -> R): R {
        val oldBlock = activeBlock
        try {
            if(oldBlock == null) activeBlock = InteractionBlock()
            return body()
        } finally {
            val block = activeBlock
            if(oldBlock == null) sendBlock(block!!)
            activeBlock = oldBlock
        }
    }

    /**
     * TODO Document
     */
    private fun sendBlock(block: InteractionBlock) {
        val message = InteractionBlockMessage(
            interactions = block.interactions.map { interaction ->
                InteractionMessage(
                    interactionNr = interaction.interactionNr,
                    subjectId = access(interaction.struct).id,
                    name = interaction.interactionName,
                    args = interaction.args.map {
                        // Think about good error messages here
                        serializer.encode(it.type, it.value)
                    }
                )
            }
        )

        blocksToSend.trySend(message)
    }

    private inline fun <T> interceptRead(read: () -> T): RPropertyClient<T> {
        val old = persistenceAny
        try {
            var prop: RPropertyClient<T>? = null
            persistence = object : PersistenceManager<Any> {
                override fun new(obj: Any?, name: String?, type: KType): Any = error("This should never happen")

                override fun set(obj: Any?, name: String?, type: KType, store: Any, value: Any?, quiet: Boolean) = error("This should never happen")

                override fun get(obj: Any?, name: String?, type: KType, store: Any, quiet: Boolean): Any? {
                    check(prop == null) { "Intercepted read but multiple properties were accessed" }
                    val struct = (obj as? RStruct.Impl) ?: error("Tried to intercept read for non struct object")
                    require(name != null) { "Tried to intercept read for non property" }
                    val descriptor = access(struct).descriptor.byName[name] ?: error("Tried to intercept read for non property")
                    access(struct).properties[descriptor.index].let {
                        prop = it as RPropertyClient<T>
                        return old.get(obj, name, type, store, true)
                    }
                }
            }
            read()
            return prop ?: error("Intercepted read but no property was accessed")
        } finally {
            persistence = old
        }
    }

    /**
     * TODO Document
     */
    internal fun handleInteraction(struct: RStruct.Impl, name: String, args: Array<out InteractionArg<*>>, predictor: (PredictorScope.() -> Unit)?): InteractionHandleClient {
        return openInteractionBlock {
            val record = InteractionRecord(
                remui = this,
                interactionNr = nextInteraction++,
                struct = struct,
                interactionName = name,
                args = args
            )
            awaitingProcessing += record
            activeBlock!!.interactions += record
            predictor?.invoke(object: PredictorScope {
                override fun <T> KProperty0<T>.setTo(value: T) {
                    interceptRead { this() }.also { record.setLocalState(it, value) }
                }
            })
            record.handle
        }
    }

    override fun <T> createRProperty(impl: RStruct.Impl, descriptor: RPropertyDescriptor, type: KType): RProperty<T> {
        return RPropertyClient<T>(
            impl,
            descriptor,
            type
        )
    }

    override fun RStruct.Impl.callInteraction(func: KFunction<InteractionHandle>, args: Array<out InteractionArg<*>>, predictor: (PredictorScope.() -> Unit)?): InteractionHandle {
        return handleInteraction(this, func.name, args, predictor)
    }

}

/**
 * TODO Document
 */
internal class InteractionRecord(
    val remui          : RemuiClient,
    val interactionNr  : Int,
    val struct         : RStruct.Impl,
    val interactionName: String,
    val args           : Array<out InteractionArg<*>>,
)
{
    /**
     * TODO Document
     */
    val handle = InteractionHandleClient(this)

    /**
     * TODO Document
     */
    internal var responseProcessed = false

    /**
     * TODO Document
     */
    internal val processedSignal = MutableSharedFlow<Unit>(replay = 1)

    /**
     * TODO Document
     */
    internal val toSync = mutableSetOf<RPropertyClient<*>>()

    /**
     * TODO Document
     */
    internal suspend fun markProcessed() {
        if(responseProcessed) return
        responseProcessed = true
        toSync.forEach { it.trySync(interactionNr) }
        processedSignal.emit(Unit)
    }

    /**
     * TODO Document
     */
    internal fun setLocalState(prop: RPropertyClient<*>, value: Any?) {
        val prop = prop as RPropertyClient<Any?>
        prop.lockTill(interactionNr)
        prop.set(value)
        toSync += prop
    }
}
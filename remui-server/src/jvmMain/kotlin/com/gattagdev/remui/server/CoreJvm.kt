/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui.server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.remember
import com.gattagdev.remui.AddDiff
import com.gattagdev.remui.InteractionArg
import com.gattagdev.remui.InteractionBlockMessage
import com.gattagdev.remui.InteractionHandle
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
import com.gattagdev.remui.getId
import com.gattagdev.remui.remuiComponentCategory
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.*
import kotlin.reflect.typeOf

internal val Remui.server: RemuiServer get() = this as RemuiServer

class RemuiServer private constructor(config: RemuiConfig): Remui(config) {


    companion object {

        /**
         * TODO Document
         */
//        context(CoroutineScope)
        suspend operator fun invoke(
            config : RemuiConfig,
            io     : RemuiIO,
            content: @Composable () -> Unit
        ) {
            val e = Executors.newSingleThreadScheduledExecutor()
            withContext(e.asCoroutineDispatcher() + YieldFrameClock) {
                val remui = RemuiServer(config)

                GlobalSnapshotManager.ensureStarted()

                val composer = Recomposer(coroutineContext)
                launch { composer.runRecomposeAndApplyChanges() }

                val applier = RemuiApplier(
                    remui,
                    { remui.persistenceAny.set(remui, "root", typeOf<RStruct.Impl?>(), remui.rootStore, it) },
                    remui::addStruct,
                    remui::removeStruct,
                    remui::performCollection
                )

                val composition = Composition(applier, composer)

                launch {
                    try {
                        composition.setContent {
                            CompositionLocalProvider(RemuiLocal provides remui) {
                                /* Read the latest nr and forces a recomposition even if no state was changed
                                   by the interaction handler. Will use Recomposer::awaitIdle in the future
                                 */
                                remui.composedLatestNr = remember(remui.latestNr){ remui.latestNr }

                                content()
                            }
                        }
//                        composer.close()
                        composer.join()
                        println("Ooops")
                    }catch (t: Throwable){
                        t.printStackTrace()
                    } finally {
                        composition.dispose()
                    }
                }

                launch {
                    for (data in io.receive) {
                        println(System.nanoTime()/1000)
                        remui.processInteractionBlock(remui.serializer.decode<InteractionBlockMessage>(data))
                    }
                }

                launch {
                    for (update in remui.outgoing) {
                        io.send.send(remui.serializer.encode(update))
                        println(System.nanoTime()/1000)
                    }
                }.join()
            }

        }

    }

    private var nextId              = 0

    private var latestNrStore       = persistenceAny.new(this, "latestNr", typeOf<Int>())
    private var latestNr: Int
        get()      = persistenceAny.get(this, "latestNr", typeOf<Int>(), latestNrStore) as Int
        set(value) { latestNrStore = persistenceAny.set(this, "latestNr", typeOf<Int>(), latestNrStore, value) }
    init { latestNr = 0 }

    internal var composedLatestNr    = 0
    private val addedStructs        = mutableSetOf<RStruct.Impl>()
    private val removedStructs      = mutableSetOf<RStruct.Impl>()
    private val updatedProperties   = mutableMapOf<RProperty<*>, PropertyUpdate>()
    private var lastRoot            = null as RStruct?
    private var lastNr              = -1

    private val outgoing = Channel<ServerUpdate>(Channel.Factory.UNLIMITED)

    private class PropertyUpdate(
        val original: Any?,
        var current: Any?
    )

    /**
     * TODO Document
     */
    fun createStruct(type: KType): RStruct.Impl {
        val cls = (type.classifier as? KClass<*>)

        require(cls?.isSubclassOf(RStruct.Impl::class) == true) { """
            |Cannot create struct for type ($type) because its classifier is not a subtype or RStruct.Impl.
            |You are probably manually calling Remui::createStruct with a weird KType.
        """.trimIndent() }

        require(!cls.isAbstract) { """
            |You cannot instantiate an instance of an abstract class. (type: $type)
        """.trimIndent() }

        check(objectPool.getOrNull(remuiComponentCategory, cls as KClass<out RStruct.Impl>) != null) { """
            |The class (${cls.qualifiedName}) is not registered for (category: ${::remuiComponentCategory.name}) in the object pool.
            |Make sure you include the config for the component module you are utilizing.
        """.trimIndent() }

        check(objectPool.getOrNull(classKey, cls) != null) { """
            |The class (${cls.qualifiedName}) is not registered for (category: ${::classKey.name}) in the object pool.
            |Make sure you include the config for the component module you are utilizing.
        """.trimIndent() }

        require(cls.typeParameters.size == type.arguments.size) { """
            |The number of type arguments provided (type: $type) does not match the type parameters specified for the class.
            |(expected: ${ cls.typeParameters.size }, provided: ${ type.arguments.size })
            |Did you manually create the KType?
        """.trimIndent() }

        val types = cls.typeParameters.mapIndexed { index, tp ->
            val t = type.arguments[index].type
            val name = tp.name

            require(t != null) { """
                |Cannot create structs with type arguments that are null. (type: $type) 
            """.trimIndent() } // TODO investigate cases for star projection and change error message (for some reason I can't download sources or docs right now)

            name to t
        }.toMap()

        val id = nextId++
        val struct = createStruct(cls, id, types)

        return struct
    }

    private fun addStruct(struct: RStruct.Impl) {
        _structs[access(struct).id] = struct
        addedStructs += struct
    }

    /**
     * TODO Document
     */
    internal fun setProperty(property: RProperty<*>, value: Any?) {
        val u = updatedProperties.getOrPut(property) {
            PropertyUpdate(property.get(true), null)
        }
        u.current = value
        property.asAny.set(value, false)
    }

    /**
     * TODO Document
     */
    private fun removeStruct(struct: RStruct.Impl) {
        removedStructs += struct
        _structs.remove(access(struct).id)
    }

    /**
     * TODO Document
     */
    private fun performCollection() {

        val root = persistenceAny.get(this, "root", typeOf<RStruct.Impl?>(), rootStore, quiet = true) as RStruct.Impl

        when {
            composedLatestNr != lastNr             -> { }
            root != lastRoot               -> { }
            addedStructs.isNotEmpty()      -> { }
            updatedProperties.isNotEmpty() -> { }
            removedStructs.isNotEmpty()    -> { }
            else -> return
        }

        val toIgnore = addedStructs.intersect(removedStructs)

//        val nr = persistenceAny.get(this, "latestNr", typeOf<Int>(), latestNrStore, quiet = true) as Int

        val update = ServerUpdate(
            interactionNr = if(composedLatestNr == lastNr) null else composedLatestNr,

            addDiffs = addedStructs.asSequence().filter { access(it).id in _structs }.map {
                AddDiff(
                    id = getId(it).id,
                    classId = objectPool[classKey, it::class],
                    types = access(it).types.map { it.key to objectPool.encode(it.value) }.toMap()
                )
            }.toList(),

            removeDiffs = removedStructs.asSequence().filter { it !in toIgnore }.map {
                RemoveDiff(id = getId(it).id)
            }.toList(),

            propDiffs = updatedProperties.asSequence()
                .filterNot { access(it.key.impl).id !in _structs || it.value.original == it.value.current }
                .map {
                    PropDiff(
                        access(it.key.impl).id,
                        it.key.descriptor.index,
                        serializer.encode(it.key.type, it.value.current)
                    )
                }.toList(),

            rootId = if(root == lastRoot) null else getId(root).id,
        )

        outgoing.trySend(update)

        lastNr = composedLatestNr
        lastRoot = root
        addedStructs.clear()
        updatedProperties.clear()
        removedStructs.clear()

    }

    /**
     * TODO Document
     */
    private fun processInteractionBlock(block: InteractionBlockMessage) {
        block.interactions.forEach { im ->

            val nr = im.interactionNr

            try {

                val subject = _structs[im.subjectId] ?: error("""
                    |Struct could not be found.
                """.trimIndent())

                val handler = access(subject).interactions[im.name] as? RInteractionHandlerServer ?: error("""
                    |Interaction handler could not be found.
                """.trimIndent())

                require(handler.types.size == im.args.size) { """
                    |Interaction handler expects a different number of arguments than provided in the InteractionMessage
                    |(expected: ${ handler.types.size }, provided: ${ im.args.size })
                """.trimIndent() }

                val types = handler.types
                val args = im.args.mapIndexed { index, it -> serializer.decode(types[index], it) }

                val func = handler.handler

                func::class.java.getMethod("invoke", *args.map { Any::class.java }.toTypedArray()).also {
                    it.isAccessible = true
                }.invoke(func, *args.toTypedArray())

            } catch(ex: Exception) {

                println("""
                    |During execution of interaction (nr: $nr) an exception occurred.
                    |${ ex.stackTraceToString() }
                    |This is non fatal.
                """.trimIndent())

            } finally {
                latestNr = nr
            }
        }
    }

    override fun <T> createRProperty(impl: RStruct.Impl, descriptor: RPropertyDescriptor, type: KType): RProperty<T> {
        return RPropertyServer<T>(impl, descriptor, type)
    }

    override fun RStruct.Impl.callInteraction(func: KFunction<InteractionHandle>, args: Array<out InteractionArg<*>>, predictor: (PredictorScope.() -> Unit)?): InteractionHandle {
        TODO("Not yet implemented")
    }
}


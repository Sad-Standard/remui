/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui

import com.gattagdev.remui.RStruct.Impl
import com.gattagdev.remui.util.newInstance
import com.gattagdev.remui.util.runWith
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.typeOf


/**
 * TODO Document
 */
data class RemuiConfig(
    val objectModule      : ObjectModule?                               = null,
    val serializerModule  : (SerializersModuleBuilder.(Remui) -> Unit)? = null,
    val remuiSerializer   : ((SerializersModule) -> RemuiSerializer)?   = null,
    val persistenceManager: PersistenceManager<*>?                      = null,
) {

    companion object {
//        fun invoke()
    }

    /**
     * TODO Document
     */
    operator fun plus(right: RemuiConfig): RemuiConfig {
        val left = this
        return RemuiConfig(
            objectModule = ObjectModule {
                left.objectModule?.construct()
                right.objectModule?.construct()
            },
            serializerModule = { remui ->
                left.serializerModule?.invoke(this, remui)
                right.serializerModule?.invoke(this, remui)
            },
            remuiSerializer = right.remuiSerializer ?: left.remuiSerializer,
            persistenceManager = right.persistenceManager ?: left.persistenceManager
        )
    }
}

/**
 * TODO Document
 */
abstract class Remui protected constructor(config: RemuiConfig) {

    /**
     * TODO Document
     */
    val objectPool: ObjectPool

    /**
     * TODO Document
     */
    val serializer: RemuiSerializer

    /**
     * TODO Document
     */
    var persistence: PersistenceManager<*>
        protected set(value) { _persistence = value }
        get() = _persistence

    private var _persistence: PersistenceManager<*>

    protected val persistenceAny get() = persistence as PersistenceManager<Any>

    protected val rootStore: Any

    /**
     * The root node in this context, serves as the entry point to graph traversal
     */
    val root: RStruct?
        get() = persistenceAny.get(this, "root", typeOf<RStruct?>(), rootStore) as RStruct?

    protected val _structs: MutableMap<Int, RStruct.Impl>

    internal val _structDescriptors: MutableMap<KClass<out RStruct.Impl>, RStructDescriptor>

    init {
        require(config.remuiSerializer    != null) { "Config must specify a RemuiSerializer factory" }
        require(config.persistenceManager != null) { "Config must specify a PersistenceManager" }

        objectPool = ObjectPool(ObjectModule{
            BaseClassModule.construct()
            config.objectModule?.construct()
        })

        val serializerModule = SerializersModule{
            include(buildSerializer(
                objectPool,
                { getId(it).id },
                { _structs[it] ?: error("TODO") } // TODO redo this completely
            ))
            config.serializerModule?.invoke(this, this@Remui)
        }

        serializer = config.remuiSerializer.invoke(serializerModule)

        _persistence = config.persistenceManager

        rootStore = persistenceAny.new(this, "root", typeOf<RStruct>())

        _structs = mutableMapOf()

        _structDescriptors = mutableMapOf()


    }

    /**
     * TODO Document
     */
    fun getStruct(id: Int): RStruct.Impl =
        _structs[id] ?: error("Struct with id $id is not present")

    /**
     * TODO Document
     */
    protected fun <RS: RStruct.Impl> createStruct(
        kClass: KClass<RS>,
        id    : Int,
        types : Map<String, KType>
    ): RS {
        require(id !in _structs) { "A struct with id $id already exists" }

        val descriptor = _structDescriptors.getOrPut(kClass) {
            RStructDescriptor(
                remui   = this,
                kClass  = kClass
            )
        }

        val context = StructInitContext(
            id         = id,
            types      = types,
            descriptor = descriptor
        )

        val struct = StructInitContext.context.runWith(context) {
            try {
                kClass.newInstance()
            } catch (t: Throwable) {
                t.printStackTrace()
                throw t
            }
        }

        return struct
    }

    internal fun <T> _createRProperty(
        impl: Impl,
        descriptor: RPropertyDescriptor,
        type: KType
    ): RProperty<T> = createRProperty(impl, descriptor, type)

    protected abstract fun <T> createRProperty(
        impl: Impl,
        descriptor: RPropertyDescriptor,
        type: KType
    ): RProperty<T>

    internal fun Impl._callInteraction(
        func: KFunction<InteractionHandle>,
        args: Array<out InteractionArg<*>>,
        predictor: (PredictorScope.() -> Unit)?
    ): InteractionHandle = callInteraction(func, args, predictor)

    protected abstract fun Impl.callInteraction(
        func: KFunction<InteractionHandle>,
        args: Array<out InteractionArg<*>>,
        predictor: (PredictorScope.() -> Unit)?
    ): InteractionHandle

}

///**
// * TODO Document
// */
//expect class Remui: RemuiBase

/**
 * TODO Document
 */
interface PersistenceManager<T: Any?> {

    /**
     * TODO Document
     */
    fun new(obj: Any?, name: String?, type: KType): T

    /**
     * TODO Document
     */
    fun set(obj: Any?, name: String?, type: KType, store: T, value: Any?, quiet: Boolean = false): T

    /**
     * TODO Document
     */
    fun get(obj: Any?, name: String?, type: KType, store: T, quiet: Boolean = false): Any?
}

data class RemuiIO(
    val send   : SendChannel<ByteArray>,
    val receive: ReceiveChannel<ByteArray>
)

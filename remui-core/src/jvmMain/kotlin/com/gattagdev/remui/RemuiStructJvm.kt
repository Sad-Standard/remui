/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "CONTEXT_RECEIVERS_DEPRECATED")
@file:OptIn(ExperimentalReflectionOnLambdas::class)

package com.gattagdev.remui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import java.util.WeakHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.reflect
import kotlin.reflect.typeOf

actual class RSInternal(actual val struct: RemuiStruct.Impl, actual val id: RemuiId, actual val types: Map<String, KType>) {

    private var nextPropId = 0
    val propertiesByName   = mutableMapOf<String, PropertyState>()
    val propertiesById     = mutableMapOf<PropId, PropertyState>()
    val handlers           = mutableMapOf<String, InteractionHandler>()

    actual fun <T> makePropertyDelegate(name: String, type: KType): ReadOnlyProperty<RemuiStruct, T> {
        val propState = PropertyState(this, name, nextPropId++, type)
        propertiesByName[name] = propState
        propertiesById[nextPropId] = propState

        return object: ReadOnlyProperty<RemuiStruct, T> {
            override fun getValue(thisRef: RemuiStruct, property: KProperty<*>): T = propState.state as T
        }

    }

    actual fun callInteraction(name: String, args: Array<out InteractionArg<*>>): InteractionHandle {
        TODO("Not yet implemented")
    }
}

class PropertyState(
    val internal: RSInternal,
    val name: String,
    val id: PropId,
    val type: KType
) {
    var state: Any? = NotInitialized
}

class InteractionHandler(
    val name: String,
    val types: List<KType>,
    val handler: Function<InteractionHandle>
)

actual class InteractionHandle()

fun getId(struct: RemuiStruct): RemuiId {
    return (struct as RemuiStruct.Impl).internal.id
}

@PublishedApi
internal class ConstructionContext(
    val id: RemuiId,
    val types: Map<String, KType>
)
@PublishedApi
internal val constructionContext = ThreadLocal<ConstructionContext?>()

actual fun initInternal(impl: RemuiStruct.Impl): RSInternal {
    val c = constructionContext.get() ?: error("Cannot directly call the RemuiStruct.Impl constructor")
    return RSInternal(impl, c.id, c.types)
}

@Immutable
actual data class RemuiId(
    val context: RemuiStructContext,
    val value: Long
)

class RemuiStructContext(
    baseObjectModule: ObjectModule,
    serializerBuilder: (SerializersModule) -> RemuiSerializer,
){
    val objectPool = ObjectPool(baseObjectModule + BaseClassModule + RemuiClassModule)

    val serializer = serializerBuilder(
        SerializersModule{
//            include(Computable.module)
            include(buildSerializer(
                objectPool,
                { (it as RemuiStruct.Impl).internal.id.value },
                { structs[it]!! }
            ))
        }

    )
//    val serializerModule = buildSerializer(
//        structInterfaces + structTypeList,
//        { (it as RemuiStruct.Impl).internal.id.value },
//        { structs[it]!! }
//    )

    internal var latestInteractionNr: Int = 0

    private var nextId: Long = 0

    @PublishedApi
    internal fun newId(): RemuiId {
        return RemuiId(this, nextId++)
    }

    inline fun <reified T: RemuiStruct.Impl> construct(): T {
        val old = constructionContext.get()
        try {
            val type = typeOf<T>()
            val id = newId()
            constructionContext.set(ConstructionContext(
                id,
                T::class.typeParameters.mapIndexed { index, tp ->
                    tp.name to type.arguments[index].type!!
                }.toMap())
            )
            return T::class.primaryConstructor!!.call()
        } finally {
            constructionContext.set(old)
        }
    }

    fun handleInteractionBlock(block: InteractionBlockMessage){
        block.interactions.forEach {
            val struct = structs[it.subjectId] as RemuiStruct.Impl
            val handler = struct.internal.handlers[it.name]
            if(handler != null) {
                val args = it.args.mapIndexed { index, arg -> serializer.decode(handler.types[index], arg) }
                val funcFound = handler.handler
                try {
                    funcFound::class.java.getMethod("invoke").invoke(funcFound, *args.toTypedArray())
                }catch (th: Throwable){
                    th.printStackTrace()
                    throw th
                }
            }
            latestInteractionNr = it.interactionNr
        }
    }

    val structs = mutableMapOf<Long, RemuiStruct>()

    val addedStructs = mutableSetOf<RemuiStruct>()
    val removedStructs = mutableSetOf<RemuiStruct>()
    val updatedProperties = mutableMapOf<PropertyState, Any?>()
    var lastRoot = null as RemuiStruct?
    var lastNr = -1

    val toSend = Channel<ServerUpdate>(UNLIMITED)

    fun add(struct: RemuiStruct){
        structs[getId(struct).value] = struct
        addedStructs += struct
    }

    fun update(prop: PropertyState, newValue: Any?) {
        if(!updatedProperties.containsKey(prop)) {
            updatedProperties[prop] = prop.state
        }
        prop.state = newValue
    }

    fun remove(struct: RemuiStruct){
        structs.remove(getId(struct).value)
        removedStructs += struct
    }
}

sealed interface DiffMessage {
    data class New(val struct: RemuiStruct): DiffMessage
    data class Update(val struct: RemuiStruct, val prop: PropertyState, val oldValue: Any?, val newValue: Any?): DiffMessage
    data class Remove(val struct: RemuiStruct): DiffMessage
}


val LocalRemuiStructContext = staticCompositionLocalOf<RemuiStructContext> { error("Default not supported") }

class RemuiMutableContext internal constructor() {
    infix fun <V> KProperty0<V>.setTo(value: V){
        val internal = (this.javaClass.getMethod("getBoundReceiver").apply {
            isAccessible = true
        }.invoke(this)!! as RemuiStruct.Impl).internal
        val propState = internal.propertiesByName[this.name]!!
        internal.id.context.update(propState, newValue = value)
    }

    infix fun <F: Function<InteractionHandle>> F.setTo(value: F){
        val internal = (this.javaClass.getMethod("getBoundReceiver").apply {
            isAccessible = true
        }.invoke(this)!! as RemuiStruct.Impl).internal
        val kFunc = (this as KFunction<*>)
        val name = kFunc.name
        internal.handlers[name] = InteractionHandler(
            name    = name,
            types   = kFunc.parameters.map { it.type },
            handler = value
        )
    }
}

@PublishedApi
internal val mutableContext = RemuiMutableContext()

class RemuiComponentSpec<T: RemuiStruct> @PublishedApi internal constructor(){
    @Composable
    inline fun <reified C: RemuiStruct> component(
        noinline update : @DisallowComposableCalls context(RemuiMutableContext) T.(List<C>) -> Unit,
        content: @Composable () -> Unit
    ){
        ReusableComposeNode<RemuiNode.Component<RemuiNode.Struct<T>>, RemuiApplier>(
            factory = {
                RemuiNode.Component({
                    val structs = it.map { (it as RemuiNode.Struct<*>).struct as C }
                    update(mutableContext, struct, structs)
                })
            },
            update  = {  },
            content = content
        )
    }
}

@Composable
inline fun <reified T: RemuiStruct.Impl> RemoteComponent(
    noinline update: @DisallowComposableCalls context(RemuiMutableContext) Updater<RemuiNode.Struct<T>>.() -> Unit,
    components: @Composable RemuiComponentSpec<T>.() -> Unit
) {
    val remuiStructContext = LocalRemuiStructContext.current
    ReusableComposeNode<RemuiNode.Struct<T>, RemuiApplier>(
        factory = { RemuiNode.Struct<T>(remuiStructContext.construct()) },
        update  = { update(mutableContext, this) },
        content = { RemuiComponentSpec<T>().components() }
    )
}

private object NotInitialized

sealed interface RemuiNode {

    fun onBeginChanges() { }
    fun onEndChanges  () { }

    fun onClear()
    fun insertBottomUp(index: Int, instance: RemuiNode)
    fun insertTopDown(index: Int, instance: RemuiNode)
    fun move(from: Int, to: Int, count: Int)
    fun remove(index: Int, count: Int)



    class Root: RemuiNode {

        var child: Struct<*>? = null

        override fun onEndChanges() {
            child
        }

        override fun onClear() {
            child?.parent = null
            child = null
        }

        override fun insertBottomUp(index: Int, instance: RemuiNode) { }

        override fun insertTopDown(index: Int, instance: RemuiNode) {
            if(child != null || index != 0) error("")
            if(instance !is Struct<*>) error("")
            instance.parent = this
            child = instance
        }

        override fun move(from: Int, to: Int, count: Int) {

        }

        override fun remove(index: Int, count: Int) {
            if(index != 0 || count > 1) error("")
            if(count == 0) return
            child?.parent = null
            child = null
        }
    }

    sealed interface NonRoot: RemuiNode {
        var parent: RemuiNode?
    }

    class Struct<T: RemuiStruct>(val struct: T): NonRoot {

        val components = mutableListOf<Component<Struct<T>>>()

        private var _parent: RemuiNode? = null

        override var parent: RemuiNode?
            get() = _parent
            set(value) {
                val old = _parent
                if(old != null && value != null) error("")
                if(old == null && value == null) error("")
                if(old != null){
                    onClear()
                    getId(struct).context.remove(struct)
                } else {
                    getId(struct).context.add(struct)
                }
                _parent = value
            }

        override fun onClear() {
            components.forEach {
                it.parent = null
            }
            components.clear()
        }

        override fun insertBottomUp(index: Int, instance: RemuiNode) { }

        override fun insertTopDown(index: Int, instance: RemuiNode) {
            val instance = instance as Component<Struct<T>>
            instance.parent = this
            components.add(index, instance)
        }

        override fun move(from: Int, to: Int, count: Int) {
            val dest = if (from > to) to else to - count
            if (count == 1) {
                if (from == to + 1 || from == to - 1) {
                    // Adjacent elements, perform swap to avoid backing array manipulations.
                    val fromEl = components[from]
                    val toEl = components.set(to, fromEl)
                    components[from] = toEl
                } else {
                    val fromEl = components.removeAt(from)
                    components.add(dest, fromEl)
                }
            } else {
                val subView = components.subList(from, from + count)
                val subCopy = subView.toMutableList()
                subView.clear()
                components.addAll(dest, subCopy)
            }
        }

        override fun remove(index: Int, count: Int) {

            repeat(count) {
                components.removeAt(index).apply {
                    parent = null
                }
            }
        }
    }

    class Component<T: NonRoot>(private val update: T.(List<NonRoot>) -> Unit): NonRoot {

        val children = mutableListOf<NonRoot>()

        private var _parent: RemuiNode? = null

        override var parent: RemuiNode?
            get() = _parent
            set(value) {
                val old = _parent
                if(old != null && value != null) error("")
                if(old == null && value == null) error("")
                if(old != null){
                    onClear()
                } else {
                }
                _parent = value
            }

        override fun onClear() {
            markDirty()
            children.forEach {
                it.parent = null
            }
            children.clear()
        }


        override fun onEndChanges() {
            if(dirty && parent != null) {
                dirty = false
                update.invoke(parent as T, children)
            }
        }

        var dirty = true

        fun markDirty() { dirty = true }

        override fun insertBottomUp(index: Int, instance: RemuiNode) { }

        override fun insertTopDown(index: Int, instance: RemuiNode) {
            markDirty()
            val instance = instance as NonRoot
            instance.parent = this
            children.add(index, instance)
        }

        override fun move(from: Int, to: Int, count: Int) {
            markDirty()
            val dest = if (from > to) to else to - count
            if (count == 1) {
                if (from == to + 1 || from == to - 1) {
                    // Adjacent elements, perform swap to avoid backing array manipulations.
                    val fromEl = children[from]
                    val toEl = children.set(to, fromEl)
                    children[from] = toEl
                } else {
                    val fromEl = children.removeAt(from)
                    children.add(dest, fromEl)
                }
            } else {
                val subView = children.subList(from, from + count)
                val subCopy = subView.toMutableList()
                subView.clear()
                children.addAll(dest, subCopy)
            }
        }

        override fun remove(index: Int, count: Int) {
            markDirty()
            repeat(count) { children.removeAt(index).apply {
                parent = null
            } }
        }
    }
}

class RemuiApplier(val structContext: RemuiStructContext) : Applier<RemuiNode> {
    private val root = RemuiNode.Root()
    private val stack = mutableListOf<RemuiNode>()
    override var current: RemuiNode = root

    override fun down(node: RemuiNode) {
        stack.add(current)
        current = node
        current.onBeginChanges()
    }

    override fun up() {
        check(stack.isNotEmpty()) { "empty stack" }
        current.onEndChanges()
        current = stack.removeLast()
    }

    override fun clear() {
        stack.clear()
        current = root
        current.onClear()
    }

    override fun onBeginChanges() {
        current.onBeginChanges()
    }

    override fun onEndChanges() {
        current.onEndChanges()

        val addDiffs    = mutableSetOf<AddDiff>()
        val propDiffs   = mutableSetOf<PropDiff>()
        val removeDiffs = mutableSetOf<RemoveDiff>()

        val objPool = structContext.objectPool
        structContext.addedStructs.forEach { addDiffs += AddDiff(
            getId(it).value,
            objPool[classKey, it::class],
            (it as RemuiStruct.Impl).internal.types.mapValues { objPool.encode(it.value) }
        ) } // TODO
        structContext.removedStructs.forEach { removeDiffs += RemoveDiff(getId(it).value) }
        structContext.updatedProperties.forEach { prop, original ->
            if(prop.state != original){
                propDiffs += PropDiff(
                    prop.internal.id.value,
                    prop.id,
                    structContext.serializer.encode(prop.type, prop.state)
                )
            }
        }

        val currentRoot = root.child!!.struct
        val currentNr = structContext.latestInteractionNr

        val willSend = false
                || addDiffs.isNotEmpty()
                || propDiffs.isNotEmpty()
                || removeDiffs.isNotEmpty()
                || (structContext.lastRoot != currentRoot)
                || (structContext.lastNr != currentNr)

        if(willSend) {
            val su = ServerUpdate(
                interactionNr = currentNr,
                addDiffs      = addDiffs,
                propDiffs     = propDiffs,
                removeDiffs   = removeDiffs,
                rootId        = getId(currentRoot).value
            )
            structContext.toSend.trySend(su)
        }

        structContext.addedStructs.clear()
        structContext.updatedProperties.clear()
        structContext.removedStructs.clear()
        structContext.lastRoot = currentRoot
        structContext.lastNr = currentNr


    }

    override fun insertBottomUp(index: Int, instance: RemuiNode) {
        current.insertBottomUp(index, instance)
    }

    override fun insertTopDown(index: Int, instance: RemuiNode) {
        current.insertTopDown(index, instance)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        current.remove(index, count)
    }
}

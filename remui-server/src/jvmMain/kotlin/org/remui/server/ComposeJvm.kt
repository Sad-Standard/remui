/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package org.remui.server

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.ReusableComposeNode
import androidx.compose.runtime.Updater
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.staticCompositionLocalOf
import org.remui.InteractionHandle
import org.remui.RStruct
import org.remui.Remui
import org.remui.access
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.set
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction4
import kotlin.reflect.KFunction5
import kotlin.reflect.KFunction6
import kotlin.reflect.KProperty0
import kotlin.reflect.typeOf

val RemuiLocal = staticCompositionLocalOf<RemuiServer> { error("Default not supported") }

@Composable
inline fun <reified T: RStruct.Impl> RemoteComponent(
    noinline update: @DisallowComposableCalls context(RemuiMutableContext) Updater<RemuiNode.Struct<T>>.() -> Unit,
    components: @Composable RemuiComponentSpec<T>.() -> Unit
) {
    val remui = RemuiLocal.current
    ReusableComposeNode<RemuiNode.Struct<T>, RemuiApplier>(
        factory = { RemuiNode.Struct<T>(remui.createStruct(typeOf<T>()) as T) },
        update  = { update(mutableContext, this) },
        content = { RemuiComponentSpec<T>().components() }
    )
}

class RemuiComponentSpec<T: RStruct> @PublishedApi internal constructor() {
    @Composable
    inline fun <reified C: RStruct> component(
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

class RemuiMutableContext internal constructor() {
    infix fun <V> KProperty0<V>.setTo(value: V){
        val struct = (this.javaClass.getMethod("getBoundReceiver").apply {
            isAccessible = true
        }.invoke(this)!! as RStruct.Impl) // TODO need proper error handling
        val propDesc = (access(struct).descriptor.byName[this.name] ?: error("TODO"))
        val prop = access(struct).properties.getOrNull(propDesc.index) ?: error("TODO")
        access(struct).remui.server.setProperty(prop.asAny, value)
    }

    fun <S: RStruct.Impl> S.handle(kFunc: KFunction1<S, InteractionHandle>, handler: () -> Unit) {
        access(this).interactions[kFunc.name] = RInteractionHandlerServer(
            types = listOf(),
            handler = handler
        )
    }

    fun <S: RStruct.Impl, P0> S.handle(kFunc: KFunction2<S, P0, InteractionHandle>, handler: (P0) -> Unit) {
        access(this).interactions[kFunc.name] = RInteractionHandlerServer(
            types = kFunc.parameters.subList(1, 2).map { it.type },
            handler = handler
        )
    }

    fun <S: RStruct.Impl, P0, P1> S.handle(kFunc: KFunction3<S, P0, P1, InteractionHandle>, handler: (P0, P1) -> Unit) {
        access(this).interactions[kFunc.name] = RInteractionHandlerServer(
            types = kFunc.parameters.subList(1, 3).map { it.type },
            handler = handler
        )
    }

    fun <S: RStruct.Impl, P0, P1, P2> S.handle(kFunc: KFunction4<S, P0, P1, P2, InteractionHandle>, handler: (P0, P1, P2) -> Unit) {
        access(this).interactions[kFunc.name] = RInteractionHandlerServer(
            types = kFunc.parameters.subList(1, 4).map { it.type },
            handler = handler
        )
    }

//    infix fun KFunction0<InteractionHandle>.setTo(value: Function0<InteractionHandle>) {
//        DomEventListener::onEvent
//        setHandlerTo(this, value)
//    }
//
//    infix fun <P0> KFunction1<P0, InteractionHandle>.setTo(value: Function1<P0, InteractionHandle>) {
//        setHandlerTo(this, value)
//    }
//
//    infix fun <P0, P1> KFunction2<P0, P1, InteractionHandle>.setTo(value: Function2<P0, P1, InteractionHandle>) {
//        setHandlerTo(this, value)
//    }

    private fun getStruct(kObj: Any): RStruct.Impl {
        return (kObj.javaClass.getMethod("getBoundReceiver").apply {
            isAccessible = true
        }.invoke(kObj)!! as RStruct.Impl)
    }


//    private fun setHandlerTo(kFunc: KFunction<*>, value: Function<InteractionHandle>) {
//        getStruct(kFunc).`r$interactions`[kFunc.name] = RInteractionHandler(
//            types = kFunc.parameters.map { it.type },
//            handler = value
//        )
//    }


//    @Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
//    infix fun <K, F: Function<InteractionHandle>> K.setTo(value: F) where K: KFunction<InteractionHandle>, K: F {
//        val struct = (this.javaClass.getMethod("getBoundReceiver").apply {
//            isAccessible = true
//        }.invoke(this)!! as RStruct.Impl) // TODO need proper error handling
//        val kFunc = (this as KFunction<*>)
//        val name = kFunc.name
//
//        struct.`r$interactions`[name] = RInteractionHandler(
//            types = kFunc.parameters.map { it.type },
//            handler = value
//        )
//    }
}

val mutableContext = RemuiMutableContext()


class RemuiApplier(
    internal val remui: Remui,
    internal val setRoot: (RStruct.Impl) -> Unit,
    internal val addNode: (RStruct.Impl) -> Unit,
    internal val removeNode: (RStruct.Impl) -> Unit,
    internal val collectUpdates: () -> Unit
) : Applier<RemuiNode>
{
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

        collectUpdates()

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

sealed interface RemuiNode {

    context(RemuiApplier) fun onBeginChanges() { }
    context(RemuiApplier) fun onEndChanges  () { }

    context(RemuiApplier) fun onClear()
    context(RemuiApplier) fun insertBottomUp(index: Int, instance: RemuiNode)
    context(RemuiApplier) fun insertTopDown(index: Int, instance: RemuiNode)
    context(RemuiApplier) fun move(from: Int, to: Int, count: Int)
    context(RemuiApplier) fun remove(index: Int, count: Int)



    class Root: RemuiNode {

        var child: Struct<*>? = null

        context(RemuiApplier)
        override fun onEndChanges() {
            setRoot(child?.struct!! as RStruct.Impl) // TODO
        }

        context(RemuiApplier)
        override fun onClear() {
            child?.setParent(null)
            child = null
        }

        context(RemuiApplier)
        override fun insertBottomUp(index: Int, instance: RemuiNode) { }

        context(RemuiApplier)
        override fun insertTopDown(index: Int, instance: RemuiNode) {
            if(child != null || index != 0) error("")
            if(instance !is Struct<*>) error("")
            instance.setParent(this)
            child = instance
        }

        context(RemuiApplier)
        override fun move(from: Int, to: Int, count: Int) {

        }

        context(RemuiApplier)
        override fun remove(index: Int, count: Int) {
            if(index != 0 || count > 1) error("")
            if(count == 0) return
            child?.setParent(null)
            child = null
        }
    }

    sealed interface NonRoot: RemuiNode {

        context(RemuiApplier)
        fun getParent(): RemuiNode?

        context(RemuiApplier)
        fun setParent(node: RemuiNode?)

//        var RemuiApplier.parent: RemuiNode?

    }

    class Struct<T: RStruct>(val struct: T): NonRoot {

        val components = mutableListOf<Component<Struct<T>>>()

        private var _parent: RemuiNode? = null

        context(RemuiApplier)
        override fun getParent(): RemuiNode? = _parent

        context(RemuiApplier)
        override fun setParent(value: RemuiNode?) {
            val old = _parent
            if(old != null && value != null) error("TODO") // TODO
            if(old == null && value == null) error("TODO") // TODO
            if(old != null){
                onClear()
                removeNode(struct as RStruct.Impl)
            } else {
                addNode(struct as RStruct.Impl)
            }
            _parent = value
        }


        context(RemuiApplier)
        override fun onClear() {
            components.forEach {
                it.setParent(null)
            }
            components.clear()
        }

        context(RemuiApplier)
        override fun insertBottomUp(index: Int, instance: RemuiNode) { }

        context(RemuiApplier)
        override fun insertTopDown(index: Int, instance: RemuiNode) {
            val instance = instance as Component<Struct<T>>
            instance.setParent(this)
            components.add(index, instance)
        }

        context(RemuiApplier)
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

        context(RemuiApplier)
        override fun remove(index: Int, count: Int) {
            repeat(count) {
                components.removeAt(index).setParent(null)
            }
        }
    }

    class Component<T: NonRoot>(private val update: T.(List<NonRoot>) -> Unit): NonRoot {

        val children = mutableListOf<NonRoot>()

        private var _parent: RemuiNode? = null

        context(RemuiApplier)
        override fun getParent(): RemuiNode? = _parent

        context(RemuiApplier)
        override fun setParent(value: RemuiNode?) {
            val old = _parent
            if(old != null && value != null) error("")
            if(old == null && value == null) error("")
            if(old != null){
                onClear()
            }
            _parent = value
        }

        context(RemuiApplier)
        override fun onClear() {
            markDirty()
            children.forEach {
                it.setParent(null)
            }
            children.clear()
        }

        context(RemuiApplier)
        override fun onEndChanges() {
            if(dirty && getParent() != null) {
                dirty = false
                update.invoke(getParent() as T, children)
            }
        }

        var dirty = true

        fun markDirty() { dirty = true }

        context(RemuiApplier)
        override fun insertBottomUp(index: Int, instance: RemuiNode) { }

        context(RemuiApplier)
        override fun insertTopDown(index: Int, instance: RemuiNode) {
            markDirty()
            val instance = instance as NonRoot
            instance.setParent(this)
            children.add(index, instance)
        }

        context(RemuiApplier)
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

        context(RemuiApplier)
        override fun remove(index: Int, count: Int) {
            markDirty()
            repeat(count) { children.removeAt(index).setParent(null) }
        }
    }
}

internal object YieldFrameClock : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(
        onFrame: (frameTimeNanos: Long) -> R
    ): R {
        yield()
        return onFrame(System.nanoTime())
    }
}

internal object GlobalSnapshotManager {
    private val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    private val started = AtomicInteger(0)
    private val sent = AtomicInteger(0)

    fun ensureStarted() {
        if (started.compareAndSet(0, 1)) {
            val channel = Channel<Unit>(1)
            scope.launch() {
                channel.consumeEach {
                    sent.compareAndSet(1, 0)
                    Snapshot.sendApplyNotifications()
                }
            }
            Snapshot.registerGlobalWriteObserver {
                if (sent.compareAndSet(0, 1)) {
                    channel.trySend(Unit)
                }
            }
        }
    }
}

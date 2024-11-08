/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.remui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.identityHashCode
import org.remui.RStruct.Impl
import org.remui.util.Context
import org.remui.util.Wrapped
import org.remui.util.substituteTypeParameters
import org.remui.util.wrappedTypeArg
import kotlinx.coroutines.flow.first
import kotlin.jvm.JvmInline
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.*


/**
 * TODO Document
 */
internal class StructInitContext(
    val id        : Int,
    val descriptor: RStructDescriptor,
    val types     : Map<String, KType>
)
{

    companion object {

        /**
         * TODO Document
         */
        internal val context = Context<StructInitContext?> { null }
    }
}

/**
 * TODO Document
 */
class RStructDescriptor(
    val remui  : Remui,
    val kClass : KClass<out Impl>,
    val byIndex: MutableList<RPropertyDescriptor>              = mutableListOf(),
    val byName : MutableMap<String, RPropertyDescriptor>       = mutableMapOf(),
//    val byKProp: MutableMap<KProperty<*>, RPropertyDescriptor> = mutableMapOf()
)
{

    /**
     * TODO Document
     */
    fun requestDescriptor(index: Int, prop: KProperty<*>, defaultProvider: (() -> Any?)?): RPropertyDescriptor {
        byIndex.getOrNull(index)?.let {
            check(it.name == prop.name) {
                """
                    |Request for same property descriptor (index: $index) with different property name (original: ${ it.name }, given: ${ prop.name }). 
                    |This is either a bug in Remui or some naughty calls have been made to [RStruct.Impl::_prop].
                """.trimIndent()
            }
            return it
        }

        check(index == byIndex.size) {
            """
                |Property descriptor requested for (index: $index, prop: $prop), but the expected next property index for (class: ${ kClass.simpleName }) is ${ byIndex.size }.
                |This is either a bug in Remui or some naughty calls have been made to [RStruct.Impl::_prop].
            """.trimMargin()
        }

        val descriptor = RPropertyDescriptor(
            struct          = this,
            index           = index,
            name            = prop.name,
            defaultProvider = defaultProvider,
//            kProperty = prop as? KProperty1<*, *> ?: error(
//                """
//                    |KProperty (prop: $prop) for delegate is not an instance property.
//                    |This is either a bug in Remui or some naughty calls have been made to [RStruct.Impl::_prop].
//                """.trimIndent()
//            )
        )

        check(descriptor.name !in byName) {
            """
                |Property descriptor requested for (index: $index) (prop: $prop), but a property with the same name is already registered at (index: ${ byName[descriptor.name]?.index }).
                |This is either a bug in Remui or some naughty calls have been made to [RStruct.Impl::_prop].
            """.trimMargin()
        }

        byIndex += descriptor

        byName[descriptor.name] = descriptor

        // TODO stop being lazy and finish the damn errors Greg

//        byKProp[descriptor.kProperty] = descriptor

        return descriptor
    }

}

/**
 * TODO Document
 */
class RPropertyDescriptor(
    val struct         : RStructDescriptor,
    val index          : Int,
    val name           : String,
    val defaultProvider: (() -> Any?)? = null,
//    val kProperty: KProperty1<*, *>
)

fun access(struct: RStruct): Impl.Access = Impl.access(struct)


/**
 * TODO Document
 */
interface RStruct {

    /**
     * TODO Document
     */
    data class Id(val remui: Remui, val id: Int)

    /**
     * TODO Document
     */
    @Stable
    abstract class Impl: RStruct {

        protected val `r$id`          : Int
        protected val `r$descriptor`  : RStructDescriptor
        protected val `r$types`       : Map<String, KType>
        protected val `r$properties`  : MutableList<RProperty<*>> // Consider replacing with nextIndex and using descriptor for access to RProperty to save on list allocation
        protected val `r$interactions`: MutableMap<String, RInteractionHandler>

        @JvmInline
        value class Access internal constructor(val struct: Impl){
            val id           get() = struct.`r$id`
            val descriptor   get() = struct.`r$descriptor`
            val types        get() = struct.`r$types`
            val properties   get() = struct.`r$properties`
            val interactions get() = struct.`r$interactions`
            val remui        get() = struct.`r$remui`
        }

        protected val `r$remui` get() = `r$descriptor`.remui

        init {
            val context = StructInitContext.context.value ?: error(
                """
                    |Only Remui can instantiate ${ this::class.simpleName }, its constructor may not be called directly.
                    |FWI RStructs can only be created using Remui's Compose framework from the server.
                """.trimMargin()
            )

            `r$id`           = context.id
            `r$descriptor`   = context.descriptor
            `r$types`        = context.types
            `r$properties`   = mutableListOf()
            `r$interactions` = mutableMapOf()

        }

        companion object {

            internal fun access(struct: RStruct) = Access(struct as Impl)

        }

        /**
         * TODO Document
         */
        protected inline fun <T, reified W: Wrapped<T>> _prop(noinline defaultProvider: (() -> T)? = null): PropertyDelegateProvider<RStruct, ReadOnlyProperty<RStruct, T>> =
            makePropertyProvider(wrappedTypeArg<W>(), defaultProvider)

        /**
         * TODO Document
         */
        protected fun <F: KFunction<InteractionHandle>> F._call(
            vararg args: InteractionArg<*>,
            predictor: (PredictorScope.() -> Unit)? = null
        ): InteractionHandle = with(`r$remui`) {
            _callInteraction(this@_call, args, predictor)
        }

        /**
         * TODO Document
         */
        protected inline fun <T, reified W: Wrapped<T>> _arg(value: T): InteractionArg<T> =
            makeInteractionArg(value, wrappedTypeArg<W>())

        override fun toString(): String = "${ this::class.simpleName }(id = ${ `r$id` })"

        override fun equals(other: Any?): Boolean {
            return other === this
        }

//        override fun hashCode(): Int {
//            return identityHashCode(this)
//        }
    }
}

/**
 * TODO Document
 */
fun getId(struct: RStruct): RStruct.Id = RStruct.Id(access(struct).remui, access(struct).id)

/**
 * TODO Document
 */
abstract class RProperty<T> protected constructor(
    val impl: Impl,
    val descriptor: RPropertyDescriptor,
    val type: KType
): ReadWriteProperty<RStruct, T>
{

    val persistenceAny get() = access(impl).remui.persistence as PersistenceManager<Any>

    var store = persistenceAny.new(impl, descriptor.name, type)

    fun get(quiet: Boolean = false): T = persistenceAny.get(impl, descriptor.name, type, store, quiet) as T

    fun set(value: T, quiet: Boolean = false) {
        store = persistenceAny.set(impl, descriptor.name, type, store, value, quiet)
    }

    override fun getValue(thisRef: RStruct, property: KProperty<*>): T = get()

    override fun setValue(thisRef: RStruct, property: KProperty<*>, value: T) = set(value)

}

/**
 * TODO Document
 */
@PublishedApi
internal fun <T> Impl.makePropertyProvider(type: KType, defaultProvider: (() -> T)?): PropertyDelegateProvider<RStruct, ReadOnlyProperty<RStruct, T>> =
    PropertyDelegateProvider { _, property ->
        val a = access(this)
        a.remui._createRProperty<T>(
            impl       = this,
            descriptor = a.descriptor.requestDescriptor(a.properties.size, property, defaultProvider),
            type       = substituteTypeParameters(type, a.types) // TODO provide good error messages related to type substitution
        ).also {
            a.properties += it
            if(defaultProvider != null){
                it.set(defaultProvider.invoke(), quiet = true)
            }
        }
    }


/**
 * TODO Document
 */
@PublishedApi
internal fun <T> Impl.makeInteractionArg(value: T, type: KType): InteractionArg<T> =
    InteractionArg(value, substituteTypeParameters(type, access(this).types))

/**
 * TODO Document
 */
class InteractionArg<T>(val value: T, val type : KType)

interface InteractionHandle {
    /**
     * TODO Document
     */
    suspend fun join() { }
}

interface RInteractionHandler

interface PredictorScope {

    infix fun <T> KProperty0<T>.setTo(value: T)

}

val remuiInterfaceCategory = ObjectPoolCategory<KClass<out RStruct>>()
val remuiComponentCategory = ObjectPoolCategory<KClass<out Impl>>()

data class ComponentSet(
    val interfaces: List<KClass<out RStruct>>,
    val components: List<KClass<out Impl>>
): ObjectModule {

    context(ObjectPoolBuilderSpec)
    override fun construct() {
        include(classKey, interfaces)
        include(classKey, components)
        include(remuiInterfaceCategory, interfaces)
        include(remuiComponentCategory, components)
    }
}

/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.gattagdev.remui

import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlin.jvm.JvmInline
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.*


typealias PropId = Int

sealed interface RemuiStruct {

    abstract class Impl: RemuiStruct {

        @PublishedApi
        internal val internal: RSInternal = initInternal(this)

        protected inline fun <T, reified W: Wrapped<T>> _prop(): PropertyDelegateProvider<RemuiStruct, ReadOnlyProperty<RemuiStruct, T>> {
            return PropertyDelegateProvider { thisRef, property ->
                internal.makePropertyDelegate<T>(property.name, substituteTypeParameters(typeOf<W>().arguments[0].type!!, internal.types))
            }
        }

        protected fun KFunction<InteractionHandle>._call(vararg args: InteractionArg<*>): InteractionHandle = internal.callInteraction(name, args)

        protected inline val <reified T> T._arg: InteractionArg<T> get() = InteractionArg(this, typeOf<T>())

        override fun toString(): String {
            return "${this::class.simpleName}(id = ${internal.id})"
        }
    }

}

@JvmInline
value class Wrapped<T>(val value: T)

class InteractionArg<T>(val value: T, val type : KType)

expect class InteractionHandle

internal expect fun initInternal(impl: RemuiStruct.Impl): RSInternal

expect class RSInternal {

    val struct: RemuiStruct.Impl

    val id: RemuiId

    val types: Map<String, KType>

    fun <T> makePropertyDelegate(name: String, type: KType): ReadOnlyProperty<RemuiStruct, T>

    fun callInteraction(name: String, args: Array<out InteractionArg<*>>): InteractionHandle

}

expect class RemuiId

val remuiInterfaceCategory = ObjectPoolCategory<KClass<out RemuiStruct>>()
val remuiComponentCategory = ObjectPoolCategory<KClass<out RemuiStruct.Impl>>()

data class ComponentSet(
    val interfaces: List<KClass<out RemuiStruct>>,
    val components: List<KClass<out RemuiStruct.Impl>>
): ObjectModule {

    context(ObjectPoolBuilderSpec)
    override fun construct() {
        include(classKey, interfaces)
        include(classKey, components)
        include(remuiInterfaceCategory, interfaces)
        include(remuiComponentCategory, components)
    }
}

/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui

import com.gattagdev.remui.util.getQualifiedName
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * TODO Document
 */
//class RemuiConfig2(
//    private val data: Map<RemuiConfigKey<*>, Any?>
//) {
//
//    companion object {
//
//        /**
//         * TODO Document
//         */
//        operator fun invoke(body: RemuiConfigSpec.() -> Unit): RemuiConfig2 {
//
//        }
//
//    }
//
//    operator fun plus(right: RemuiConfig2): RemuiConfig2 {
//
//    }
//}
//
///**
// * TODO Document
// */
//interface RemuiConfigSpec {
//    fun <T> contribute(
//        key  : RemuiConfigKey<T>,
//        value: T
//    )
//}
//
//class RemuiConfigKey<T>(
//             val name: String,
//    internal val fold: (List<T>) -> T
//)
//
//
//
///**
// * TODO Document
// */
//fun <T> configKey(fold: (List<T>) -> T) = PropertyDelegateProvider<_, _>{ _: Any?, prop: KProperty<*> ->
//    val key = RemuiConfigKey(prop.name, fold)
//    object: ReadOnlyProperty<Any?, RemuiConfigKey<T>> {
//        override fun getValue(thisRef: Any?, property: KProperty<*>): RemuiConfigKey<T> = key
//    }
//}
//
///**
// * TODO Document
// */
//fun <T> distinctListConfigKey() = configKey<List<T>> { it.flatten().distinct() }
//
///**
// * TODO Document
// */
//val configLocatorKey by distinctListConfigKey<ConfigLocator>()
//
///**
// * TODO Document
// */
//class ConfigLocator(
//    val module       : String,
//    val qualifiedName: String,
//    val type         : Type
//)
//{
//
//    enum class Type { COMMON, SERVER, CONFIG }
//
//}
//
//fun locatableConfig(module: String, type: ConfigLocator.Type, vararg dependsOn: RemuiConfig2, body: RemuiConfigSpec.() -> Unit) = PropertyDelegateProvider<_, _> { _: Any?, prop: KProperty<*> ->
//
//    val location = ConfigLocator(
//        module        = module,
//        type          = type,
//        qualifiedName = prop.getQualifiedName()
//    )
//
//    val config = RemuiConfig2 {
//        contribute(configLocatorKey, listOf(location))
//        body()
//    }
//
//    val combined = (dependsOn.toList() + config).fold(RemuiConfig2 { }) { a, b -> a + b }
//
//    object: ReadOnlyProperty<Any?, RemuiConfig2> {
//        override fun getValue(thisRef: Any?, property: KProperty<*>): RemuiConfig2 = combined
//    }
//
//}




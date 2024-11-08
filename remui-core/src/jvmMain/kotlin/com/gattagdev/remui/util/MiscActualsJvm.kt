/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui.util

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.createType
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaGetter

actual fun makeKType(
    arguments: List<KTypeProjection>,
    classifier: KClassifier?,
    isMarkedNullable: Boolean
): KType {
    return classifier!!.createType(
        arguments,
        isMarkedNullable,
        listOf()
    )
}

actual fun <T: Any> KClass<T>.newInstance(): T = this.createInstance()

actual fun KProperty<*>.getQualifiedName(): String {
    return this.javaGetter!!.declaringClass.packageName + ".$name"
}


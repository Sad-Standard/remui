/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.util

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

/**
 * TODO Document
 */
expect fun makeKType(
    arguments: List<KTypeProjection>,
    classifier: KClassifier?, // TODO make a conscious consideration about nullability
    isMarkedNullable: Boolean
): KType


/**
 * TODO Document
 */
expect fun <T: Any> KClass<T>.newInstance(): T

expect fun KProperty<*>.getQualifiedName(): String

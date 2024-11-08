/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui.util

import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection

fun substituteTypeParameters(type: KType, substitutions: Map<String, KType>): KType {
    val cls = type.classifier
    if (cls is KTypeParameter) substitutions[cls.name]?.let { return it }

    val newArguments = type.arguments.map { arg ->
        val ot = arg.type
        if (ot != null) KTypeProjection(arg.variance, substituteTypeParameters(ot, substitutions))
        else arg
    }

    return makeKType(newArguments, type.classifier, type.isMarkedNullable)
}
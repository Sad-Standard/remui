/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui

import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

actual fun makeKType(
    arguments: List<KTypeProjection>,
    classifier: KClassifier?,
    isMarkedNullable: Boolean
): KType {
    return object: KType {
        override val arguments       : List<KTypeProjection> = arguments
        override val classifier      : KClassifier?          = classifier
        override val isMarkedNullable: Boolean               = isMarkedNullable
    }
}
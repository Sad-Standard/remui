/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.util

import kotlin.jvm.JvmInline
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * TODO Document
 */
@JvmInline
value class Wrapped<T>(val value: T)

/**
 * TODO Document
 */
inline fun <reified W: Wrapped<*>> wrappedTypeArg(): KType = typeOf<W>().arguments[0].type ?: error("Wrapped type argument T cannot be null")
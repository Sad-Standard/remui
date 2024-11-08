/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.util

/**
 * TODO Document
 */
expect class Context<T> internal constructor(init: () -> T) {

    companion object

    /**
     * TODO Document
     */
    var value: T

}

/**
 * TODO Document
 */
inline fun <T, R> Context<T>.runWith(newValue: T, func: () -> R): R {
    val old = value
    try {
        value = newValue
        return func()
    } finally {
        value = old
    }
}

/**
 * TODO Document
 */
fun <T> Context.Companion.invoke(): Context<T?> = Context { null }

/**
 * TODO Document
 */
fun <T> Context.Companion.invoke(init: () -> T) = Context(init)
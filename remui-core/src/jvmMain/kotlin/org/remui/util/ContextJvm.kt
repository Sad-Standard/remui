/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.util

actual class Context<T> internal actual constructor(init: () -> T) {

    private val local = ThreadLocal.withInitial(init)

    actual var value: T
        get() = local.get()
        set(value) = local.set(value)

    actual companion object
}
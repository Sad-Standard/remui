/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.server

import org.remui.InteractionArg
import org.remui.InteractionHandle
import org.remui.RInteractionHandler
import org.remui.RProperty
import org.remui.RPropertyDescriptor
import org.remui.RStruct.Impl
import kotlin.reflect.KFunction
import kotlin.reflect.KType

class RPropertyServer<T> internal constructor(
    impl: Impl,
    descriptor: RPropertyDescriptor,
    type: KType
): RProperty<T>(impl, descriptor, type) {
}

internal val RProperty<*>.asAny: RPropertyServer<Any?> get() = this as RPropertyServer<Any?>


/**
 * TODO Document for server
 */
class InteractionHandleServer: InteractionHandle


class RInteractionHandlerServer(
    val types: List<KType>,
    val handler: Function<Unit>
): RInteractionHandler
/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.client

import org.remui.RProperty
import org.remui.RPropertyDescriptor
import org.remui.RStruct
import kotlinx.coroutines.flow.first
import org.remui.InteractionHandle
import org.remui.RInteractionHandler
import kotlin.reflect.KType

internal class RPropertyClient<T> internal constructor(
    impl: RStruct.Impl,
    descriptor: RPropertyDescriptor,
    type: KType
): RProperty<T>(impl, descriptor, type)
{

    private var _truth  : T?  = null
    private var syncAtNr: Int = -1

    /**
     * TODO Document
     */
    fun lockTill(interactionNr: Int) {
        if(interactionNr >= syncAtNr) syncAtNr = interactionNr
    }

    /**
     * TODO Document
     */
    fun trySync(interactionNr: Int) {
        if(interactionNr >= syncAtNr)
            set(_truth as T)
    }

    fun setTruth(value: T) {
        _truth = value
    }

}

//@PublishedApi
//internal actual fun Impl.callInteraction(func: KFunction<InteractionHandle>, args: Array<out InteractionArg<*>>): InteractionHandle {
//
//}

/**
 * TODO Document for client
 */
class InteractionHandleClient internal constructor(internal val record: InteractionRecord): InteractionHandle {

    override suspend fun join() {
        if(record.responseProcessed) return
        record.processedSignal.first()
    }

}




class RInteractionHandlerClient: RInteractionHandler



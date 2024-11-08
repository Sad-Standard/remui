/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui.client

import com.gattagdev.remui.RProperty
import com.gattagdev.remui.RPropertyDescriptor
import com.gattagdev.remui.RStruct
import kotlinx.coroutines.flow.first
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
class InteractionHandleClient internal constructor(internal val record: InteractionRecord): com.gattagdev.remui.InteractionHandle {

    override suspend fun join() {
        if(record.responseProcessed) return
        record.processedSignal.first()
    }

}




class RInteractionHandlerClient: com.gattagdev.remui.RInteractionHandler



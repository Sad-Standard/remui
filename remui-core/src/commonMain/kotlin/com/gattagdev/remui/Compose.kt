/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot.Companion.withoutReadObservation
import kotlin.reflect.KType

val ComposePersistenceConfig = RemuiConfig(
    persistenceManager = ComposePersistenceManager
)

object ComposePersistenceManager: PersistenceManager<MutableState<*>> {
    override fun new(obj: Any?, name: String?, type: KType): MutableState<*> {
//        println("Created state for (prop: $name)")
        return mutableStateOf(null)
    }

    override fun set(obj: Any?, name: String?, type: KType, store: MutableState<*>, value: Any?, quiet: Boolean): MutableState<*> {
//        println("Observed write of (prop: $name)")
        (store as MutableState<Any?>).value = value
        return store
    }

    override fun get(obj: Any?, name: String?, type: KType, store: MutableState<*>, quiet: Boolean): Any? {
        return if(quiet) withoutReadObservation {
//            println("Non-observed read of (prop: $name)")
            store.value
        }
        else {
//            println("Observed read of (prop: $name)")
            store.value
        }
    }
}
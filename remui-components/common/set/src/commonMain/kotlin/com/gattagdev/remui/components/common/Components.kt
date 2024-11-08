/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui.components.common

import com.gattagdev.remui.ComponentSet
import com.gattagdev.remui.RStruct

val CommonComponents = ComponentSet(
    listOf(),
    listOf(
        RemoteValue::class
    )
)


class RemoteValue<T>: RStruct.Impl(){
    val remValue: T by _prop()
}
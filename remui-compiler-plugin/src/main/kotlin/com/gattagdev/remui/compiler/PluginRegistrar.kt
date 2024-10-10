/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalCompilerApi::class)

package com.gattagdev.remui.compiler

import com.gattagdev.remui.compiler.util.*
import org.jetbrains.kotlin.compiler.plugin.*


class PluginRegistrar: QuickPluginRegistrar({
    firRegistrar {
        statusTransformer {

        }
    }
})
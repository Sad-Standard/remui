/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalCompilerApi::class)

package org.remui.compiler

import org.remui.compiler.util.*
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.fir.copy
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.getAnnotationStringValue
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

val InvertedInfix = ClassId(FqName("com.gattagdev.playground"), Name.identifier("InvertedInfix"))
val Replace = ClassId(FqName("com.gattagdev.playground"), Name.identifier("Replace"))

class PluginRegistrar: QuickPluginRegistrar({
    firRegistrar {
        statusTransformer {
            val pred = lookupPredicate { annotated(InvertedInfix.asSingleFqName()) }

            simpleFunctionStatus {
                originalStatus.let { if(matches(pred)) it.copy(isInfix = !it.isInfix) else it }
            }

        }
    }

    irGenExtension { module, _ ->
        module.transform(null as String?) {
            visitConst {
                if(it != null && "<replace>" == value as? String) {
                    (this as IrConst<String>).value = it
                }
                this
            }
            visitFunction {
                visitChildren(this, getAnnotation(Replace.asSingleFqName())?.getAnnotationStringValue() ?: it)
                this
            }
        }
    }
})
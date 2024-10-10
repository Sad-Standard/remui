/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package com.gattagdev.remui.compiler.util

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirBackingField
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirEnumEntry
import org.jetbrains.kotlin.fir.declarations.FirField
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.FirTypeAlias
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol

@JvmInline
value class OriginalStatus(val status: FirDeclarationStatus)

@JvmInline
value class ContainingClass(val containingClass: FirClassLikeSymbol<*>?)

@JvmInline
value class ContainingProperty(val containingProperty: FirProperty?)

@JvmInline
value class IsLocal(val isLocal: Boolean)

typealias StatusTransformer<T> = context(OriginalStatus, ContainingClass, ContainingProperty, IsLocal) T.() -> FirDeclarationStatus

fun interface Factory: () -> Unit

class StatusTransformerSpec() {

    fun allStatus             (transformer: StatusTransformer<FirDeclaration>     ): Unit = TODO()

    fun propertyStatus        (transformer: StatusTransformer<FirProperty>        ): Unit = TODO()

    fun simpleFunctionStatus  (transformer: StatusTransformer<FirSimpleFunction>  ): Unit = TODO()

    fun regularClassStatus    (transformer: StatusTransformer<FirRegularClass>    ): Unit = TODO()

    fun typeAliasStatus       (transformer: StatusTransformer<FirTypeAlias>       ): Unit = TODO()

    fun propertyAccessorStatus(transformer: StatusTransformer<FirPropertyAccessor>): Unit = TODO()

    fun constructorStatus     (transformer: StatusTransformer<FirConstructor>     ): Unit = TODO()

    fun fieldStatus           (transformer: StatusTransformer<FirField>           ): Unit = TODO()

    fun backingFieldStatus    (transformer: StatusTransformer<FirBackingField>    ): Unit = TODO()

    fun enumEntryStatus       (transformer: StatusTransformer<FirEnumEntry>       ): Unit = TODO()

}

class QuickStatusTransformer(
    session: FirSession
): FirStatusTransformerExtension(session){
    override fun needTransformStatus(declaration: FirDeclaration): Boolean {
        TODO("Not yet implemented")
    }
//    la

}
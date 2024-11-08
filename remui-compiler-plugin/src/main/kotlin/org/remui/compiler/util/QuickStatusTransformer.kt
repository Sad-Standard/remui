/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package org.remui.compiler.util

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.isLocalMember
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
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import kotlin.reflect.KClass

@JvmInline value class OriginalStatus    (val originalStatus    : FirDeclarationStatus  )
@JvmInline value class ContainingClass   (val containingClass   : FirClassLikeSymbol<*>?)
@JvmInline value class ContainingProperty(val containingProperty: FirProperty?          )
@JvmInline value class IsLocal           (val isLocal           : Boolean               )

typealias StatusTransformer<T> = context(OriginalStatus, ContainingClass, ContainingProperty, IsLocal) T.() -> FirDeclarationStatus


@QuickPluginDsl
class StatusTransformerSpec() {

    private var _predicates = mutableListOf<(FirDeclarationPredicateRegistrar.() -> Unit)>()

    fun declarationPredicate(body: DeclarationPredicate.BuilderContext.() -> DeclarationPredicate): DeclarationPredicate {
        val pred = DeclarationPredicate.create { body() }
        _predicates += { register(pred) }
        return pred
    }

    fun lookupPredicate(body: LookupPredicate.BuilderContext.() -> LookupPredicate): LookupPredicate {
        val pred = LookupPredicate.create { body() }
        _predicates += { register(pred) }
        return pred
    }

    fun withPredicates(body: FirDeclarationPredicateRegistrar.() -> Unit) { _predicates += body }

    private var _allStatus             : StatusTransformer<FirDeclaration>?      = null
    private var _propertyStatus        : StatusTransformer<FirProperty>?         = null
    private var _simpleFunctionStatus  : StatusTransformer<FirSimpleFunction>?   = null
    private var _regularClassStatus    : StatusTransformer<FirRegularClass>?     = null
    private var _typeAliasStatus       : StatusTransformer<FirTypeAlias>?        = null
    private var _propertyAccessorStatus: StatusTransformer<FirPropertyAccessor>? = null
    private var _constructorStatus     : StatusTransformer<FirConstructor>?      = null
    private var _fieldStatus           : StatusTransformer<FirField>?            = null
    private var _backingFieldStatus    : StatusTransformer<FirBackingField>?     = null
    private var _enumEntryStatus       : StatusTransformer<FirEnumEntry>?        = null

    fun allStatus             (transformer: StatusTransformer<FirDeclaration>     ) { _allStatus              = transformer }
    fun propertyStatus        (transformer: StatusTransformer<FirProperty>        ) { _propertyStatus         = transformer }
    fun simpleFunctionStatus  (transformer: StatusTransformer<FirSimpleFunction>  ) { _simpleFunctionStatus   = transformer }
    fun regularClassStatus    (transformer: StatusTransformer<FirRegularClass>    ) { _regularClassStatus     = transformer }
    fun typeAliasStatus       (transformer: StatusTransformer<FirTypeAlias>       ) { _typeAliasStatus        = transformer }
    fun propertyAccessorStatus(transformer: StatusTransformer<FirPropertyAccessor>) { _propertyAccessorStatus = transformer }
    fun constructorStatus     (transformer: StatusTransformer<FirConstructor>     ) { _constructorStatus      = transformer }
    fun fieldStatus           (transformer: StatusTransformer<FirField>           ) { _fieldStatus            = transformer }
    fun backingFieldStatus    (transformer: StatusTransformer<FirBackingField>    ) { _backingFieldStatus     = transformer }
    fun enumEntryStatus       (transformer: StatusTransformer<FirEnumEntry>       ) { _enumEntryStatus        = transformer }


    companion object {
        fun build(session: FirSession, builder: context(FirSessionContext) StatusTransformerSpec.() -> Unit): FirStatusTransformerExtension {

            return object: FirStatusTransformerExtension(session) {

                private val sts by lazy {
                    StatusTransformerSpec().apply { builder(FirSessionContext(session), this) }
                }

                private val toMatch: List<KClass<out FirDeclaration>> = mutableListOf<KClass<out FirDeclaration>>().apply {
                    addMatcher(sts._propertyStatus        )
                    addMatcher(sts._simpleFunctionStatus  )
                    addMatcher(sts._regularClassStatus    )
                    addMatcher(sts._typeAliasStatus       )
                    addMatcher(sts._propertyAccessorStatus)
                    addMatcher(sts._constructorStatus     )
                    addMatcher(sts._fieldStatus           )
                    addMatcher(sts._backingFieldStatus    )
                    addMatcher(sts._enumEntryStatus       )
                }

                private inline fun <
                        reified D: FirDeclaration,
                        ST: ((OriginalStatus, ContainingClass, ContainingProperty, IsLocal, D) -> FirDeclarationStatus)?
                        > MutableList<KClass<out FirDeclaration>>.addMatcher(prop: ST) {
                    if(prop != null) this += D::class
                }

                override fun needTransformStatus(declaration: FirDeclaration): Boolean = toMatch.any { it.isInstance(declaration) }

                override fun FirDeclarationPredicateRegistrar.registerPredicates() = sts._predicates.forEach { it() }

                private fun <D: FirDeclaration> (context(OriginalStatus, ContainingClass, ContainingProperty, IsLocal) D.() -> FirDeclarationStatus)?.processStatus(
                    status            : FirDeclarationStatus,
                    decl              : D,
                    containingClass   : FirClassLikeSymbol<*>?,
                    containingProperty: FirProperty?,
                    isLocal           : Boolean,
                ): FirDeclarationStatus
                {
                    val s = OriginalStatus(status)
                    val c = ContainingClass(containingClass)
                    val p = ContainingProperty(containingProperty)
                    val l = IsLocal(isLocal)

                    return when {
                        this != null           -> this.invoke(s, c, p, l, decl)
                        sts._allStatus != null -> sts._allStatus!!.invoke(s, c, p, l, decl)
                        else                   -> status
                    }
                }

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    declaration: FirDeclaration,
                ): FirDeclarationStatus = sts._allStatus?.invoke(
                    OriginalStatus(status),
                    ContainingClass(null),
                    ContainingProperty(null),
                    IsLocal(declaration.isLocalMember), // No clue if this is correct
                    declaration
                ) ?: status

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    property: FirProperty,
                    containingClass: FirClassLikeSymbol<*>?,
                    isLocal: Boolean,
                ): FirDeclarationStatus = sts._propertyStatus.processStatus(
                    status,
                    property,
                    containingClass,
                    null,
                    isLocal
                )

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    function: FirSimpleFunction,
                    containingClass: FirClassLikeSymbol<*>?,
                    isLocal: Boolean,
                ): FirDeclarationStatus = sts._simpleFunctionStatus.processStatus(
                    status,
                    function,
                    containingClass,
                    null,
                    isLocal
                )

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    regularClass: FirRegularClass,
                    containingClass: FirClassLikeSymbol<*>?,
                    isLocal: Boolean,
                ): FirDeclarationStatus = sts._regularClassStatus.processStatus(
                    status,
                    regularClass,
                    containingClass,
                    null,
                    isLocal
                )

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    typeAlias: FirTypeAlias,
                    containingClass: FirClassLikeSymbol<*>?,
                    isLocal: Boolean,
                ): FirDeclarationStatus = sts._typeAliasStatus.processStatus(
                    status,
                    typeAlias,
                    containingClass,
                    null,
                    isLocal
                )

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    propertyAccessor: FirPropertyAccessor,
                    containingClass: FirClassLikeSymbol<*>?,
                    containingProperty: FirProperty?,
                    isLocal: Boolean,
                ): FirDeclarationStatus = sts._propertyAccessorStatus.processStatus(
                    status,
                    propertyAccessor,
                    containingClass,
                    containingProperty,
                    isLocal
                )

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    constructor: FirConstructor,
                    containingClass: FirClassLikeSymbol<*>?,
                    isLocal: Boolean,
                ): FirDeclarationStatus = sts._constructorStatus.processStatus(
                    status,
                    constructor,
                    containingClass,
                    null,
                    isLocal
                )

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    field: FirField,
                    containingClass: FirClassLikeSymbol<*>?,
                    isLocal: Boolean,
                ): FirDeclarationStatus = sts._fieldStatus.processStatus(
                    status,
                    field,
                    containingClass,
                    null,
                    isLocal
                )

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    backingField: FirBackingField,
                    containingClass: FirClassLikeSymbol<*>?,
                    isLocal: Boolean,
                ): FirDeclarationStatus = sts._backingFieldStatus.processStatus(
                    status,
                    backingField,
                    containingClass,
                    null,
                    isLocal
                )

                override fun transformStatus(
                    status: FirDeclarationStatus,
                    enumEntry: FirEnumEntry,
                    containingClass: FirClassLikeSymbol<*>?,
                    isLocal: Boolean,
                ): FirDeclarationStatus = sts._enumEntryStatus.processStatus(
                    status,
                    enumEntry,
                    containingClass,
                    null,
                    isLocal
                )
            }
        }
    }
}


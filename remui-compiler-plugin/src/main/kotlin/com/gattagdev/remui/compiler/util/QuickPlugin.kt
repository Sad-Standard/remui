/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalCompilerApi::class)
@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package com.gattagdev.remui.compiler.util

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.extensions.predicate.AbstractPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

@QuickPluginDsl
class PluginRegistrarSpec(
    private val storage: CompilerPluginRegistrar.ExtensionStorage,
    private val configuration: CompilerConfiguration
) {

    @QuickPluginDsl
    fun irGenExtension(generateFunc: IrGenerationExtension.(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) -> Unit) {
        with(storage) { IrGenerationExtension.registerExtension(object: IrGenerationExtension{
            override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
                generateFunc(moduleFragment, pluginContext)
            }
        }) }
    }

    @QuickPluginDsl
    fun firRegistrar(builder: FirExtensionRegistrarSpec.() -> Unit) {
        with(storage) { FirExtensionRegistrarAdapter.registerExtension(FirExtensionRegistrarSpec().apply(builder).build()) }
    }

}

@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class QuickPluginDsl

@QuickPluginDsl
class FirExtensionRegistrarSpec() {

    private val statusTransformers = mutableListOf<context(FirSessionContext) StatusTransformerSpec.() -> Unit>()

    fun statusTransformer(builder: context(FirSessionContext) StatusTransformerSpec.() -> Unit) { statusTransformers += builder }

    internal fun build(): FirExtensionRegistrar {
        return object: FirExtensionRegistrar() {
            override fun ExtensionRegistrarContext.configurePlugin() {

                statusTransformers.forEach {
                    +{ session: FirSession ->
                        StatusTransformerSpec.build(session, it)
                    }
                }
//                +{ session: FirSession ->
//
//                    object: FirDeclarationGenerationExtension(session){
//                        override fun generateNestedClassLikeDeclaration(owner: FirClassSymbol<*>, name: Name, context: NestedClassGenerationContext): FirClassLikeSymbol<*>? {
//                            createNestedClass
//                        }
//                    }
//                }

            }

        }
    }
}

abstract class QuickPluginRegistrar constructor(
    private val extensionsRegistrar: context(PluginRegistrarSpec) ExtensionStorage.(CompilerConfiguration) -> Unit
): CompilerPluginRegistrar() {

    final override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) = extensionsRegistrar(
        PluginRegistrarSpec(this, configuration),
        this,
        configuration
    )

    final override val supportsK2 get() = true

}


@JvmInline value class FirSessionContext(val session: FirSession)

context(FirSessionContext)
fun FirDeclaration.hasAnnotation(classId: ClassId) = hasAnnotation(classId, session)

context(FirSessionContext)
fun FirDeclaration.matches(predicate: AbstractPredicate<*>) = session.predicateBasedProvider.matches(predicate, this)


sealed interface FirInst<D: FirDeclaration> {

}

interface FirSource<D: FirDeclaration> {

    context(FirSessionContext) val predicates: Set<LookupPredicate>
    context(FirSessionContext) val instances : List<FirInst<D>>
}


interface FirMutator<D: FirDeclaration>: FirSpec {

}

interface FirSpec {

    infix fun <D: FirDeclaration> FirSource<D>.mutate(mutator: FirMutator<D>.() -> Unit)

}




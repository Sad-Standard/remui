/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:OptIn(ExperimentalCompilerApi::class)

package com.gattagdev.remui.compiler.util

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class PluginRegistrarSpec(
    private val storage: CompilerPluginRegistrar.ExtensionStorage,
    private val configuration: CompilerConfiguration
) {
    fun irGenExtension(generateFunc: IrGenerationExtension.(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) -> Unit) {
        with(storage) { IrGenerationExtension.registerExtension(object: IrGenerationExtension{
            override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
                generateFunc(moduleFragment, pluginContext)
            }
        }) }
    }

    fun firRegistrar(builder: FirExtensionRegistrarSpec.() -> Unit) {
        with(storage) { FirExtensionRegistrarAdapter.registerExtension(FirExtensionRegistrarSpec().apply(builder).build()) }
    }
}

class FirExtensionRegistrarSpec(

) {

//    fun submit(builder: )

    fun statusTransformer() {

    }

    internal fun build(): FirExtensionRegistrar {

        TODO()
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

class QuickFirExtensionRegistrar(
    private val builder: FirExtensionRegistrarSpec.() -> Unit
): FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        TODO("Not yet implemented")
    }
}

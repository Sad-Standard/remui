/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */


import buildsrc.conventions.composeRuntimeVersion
import buildsrc.conventions.ktor
import org.gradle.kotlin.dsl.components
import org.gradle.kotlin.dsl.resources

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    buildsrc.conventions.`remui-kmp-all`
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose") version "1.7.0"
}

dependencies {
//    kotlinCompilerPluginClasspath(project(":remui-compiler-plugin"))
    jvmMainImplementation(compose.runtime)
    jvmMainImplementation(compose.foundation)
    jvmMainImplementation(compose.material)
    jvmMainImplementation(compose.ui)
    jvmMainImplementation(compose.components.resources)
    jvmMainImplementation(compose.components.uiToolingPreview)
}

kotlin {


    sourceSets {
        fun KotlinDependencyHandler.remui(vararg names: String) = names.forEach { implementation(project(":remui-$it")) }

        val commonMain by getting {
            dependencies {
                remui("core", "ktor-core", "html-set", "json", "protobuf")
                implementation("org.jetbrains.compose.runtime:runtime:$composeRuntimeVersion")

            }
        }

        val jvmMain by getting {
            dependencies {
                remui("ktor-server", "html-server")
//                implementation("org.jetbrains.compose.runtime:runtime:$composeRuntimeVersion")

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

            }
            compilerOptions {
                freeCompilerArgs.apply {
                    add("-P")
                    add("plugin:androidx.compose.compiler.plugins.kotlin:liveLiteralsEnabled=true")
                }
            }
        }

        val jsMain by getting {
            dependencies {
                remui("client", "html-client-compose")
                implementation(ktor("ktor-client-core"))
                implementation(ktor("ktor-client-js"))
                implementation("org.jetbrains.compose.web:web-core:$composeRuntimeVersion")
            }
        }

    }
}
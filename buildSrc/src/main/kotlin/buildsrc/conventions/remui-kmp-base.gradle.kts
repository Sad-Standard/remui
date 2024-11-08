/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package buildsrc.conventions

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("buildsrc.conventions.remui-base")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
//    kotlin("plugin.compose")
//    id("org.jetbrains.kotlin.plugin.compose")
//    id("io.kotest.multiplatform")
}

kotlin {

    sourceSets {

        val commonMain by getting {

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
            }

        }

        val `commonTest` by getting {

            dependencies {
                implementation(kotest("kotest-framework-engine"))
                implementation(kotest("kotest-assertions-core"))

                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }

        }

    }

    compilerOptions {

        languageVersion.set(KotlinVersion.KOTLIN_2_0)

        freeCompilerArgs.apply {

            add("-Xallow-kotlin-package")
            add("-Xcontext-receivers")

            add("-Xjvm-default=all")
            add("-Xlambdas=class")

        }
    }

    jvmToolchain(21)
}
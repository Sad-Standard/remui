/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

import buildsrc.conventions.kotlinVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.apply{
            add("-Xjvm-default=all")
            add("-Xallow-kotlin-package")
            add("-Xlambdas=class")
            add("-Xcontext-receivers")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")

}
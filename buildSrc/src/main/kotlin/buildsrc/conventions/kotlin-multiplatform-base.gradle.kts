/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package buildsrc.conventions

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}


kotlin {

    sourceSets {
        val `commonTest` by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:5.9.1")
                implementation("io.kotest:kotest-assertions-core:5.9.1")
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
    jvmToolchain(21)
}


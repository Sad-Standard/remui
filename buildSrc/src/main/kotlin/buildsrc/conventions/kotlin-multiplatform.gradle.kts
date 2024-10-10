/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package buildsrc.conventions

plugins {
    id("buildsrc.conventions.kotlin-multiplatform-base")
}


kotlin {
    jvm()
    js(IR) {
        browser{
            binaries.executable()
        }
        nodejs()
    }

    sourceSets {
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:5.9.1")
            }
        }
    }

}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = false
    }
}
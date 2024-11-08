/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */


import buildsrc.conventions.composeRuntimeVersion
import buildsrc.conventions.coroutines

plugins {
    buildsrc.conventions.`remui-server`
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":remui-core"))
                implementation(coroutines("kotlinx-coroutines-android"))
                implementation("org.jetbrains.compose.runtime:runtime:$composeRuntimeVersion")
                implementation("androidx.collection:collection:1.4.0")
            }
        }
    }

}
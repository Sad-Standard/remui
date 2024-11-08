/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package buildsrc.conventions

plugins {
    id("buildsrc.conventions.remui-kmp-base")
}

val componentDependencies = gradle.extra["componentDependencies"] as Map<String, List<String>>
val dependenciesList = componentDependencies[name] ?: emptyList()

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                dependenciesList.forEach { api(project(":$it")) }
                implementation(project(":remui-core"))
            }
        }
    }
}
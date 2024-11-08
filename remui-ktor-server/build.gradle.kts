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
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

plugins {
    buildsrc.conventions.`remui-server`
}


kotlin {
    sourceSets {

        val jvmMain by getting {
            dependencies {
                api(project(":remui-server"))
                api(project(":remui-ktor-core"))
                api(ktor("ktor-server-core-jvm"))
                api(ktor("ktor-server-netty-jvm"))
                api(ktor("ktor-server-cors-jvm"))
                api(ktor("ktor-server-websockets-jvm"))
            }
        }

    }
}
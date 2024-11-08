/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

import buildsrc.conventions.serialization

plugins {
    buildsrc.conventions.`remui-kmp-all`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":remui-core"))
                api(serialization("kotlinx-serialization-protobuf"))
            }
        }
    }
}
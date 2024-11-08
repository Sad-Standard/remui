/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

//import org.gradle.kotlin.dsl.version
import buildsrc.conventions.composeRuntimeVersion
import buildsrc.conventions.coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import buildsrc.conventions.ktor

plugins {
    buildsrc.conventions.`remui-kmp-all`

//    id("io.ktor.plugin") version "3.0.0-rc-1"

}



kotlin {

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.compose.runtime:runtime:$composeRuntimeVersion")
//                implementation(kotlin("reflect"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.7.1")
            }
        }
        val jvmMain by getting {
//            dependencies {
////                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
//                implementation(ktor("ktor-server-core-jvm"))
//                implementation(ktor("ktor-server-netty-jvm"))
//                implementation(ktor("ktor-server-cors-jvm"))
//                implementation(ktor("ktor-server-websockets-jvm"))
////                implementation("org.jetbrains.compose.runtime:runtime:1.6.11")
//            }
        }
        val jsMain by getting {
//            dependencies {
//                implementation("org.jetbrains.compose.web:web-core:1.6.11")
//                implementation(ktor("ktor-client-core"))
//                implementation(ktor("ktor-client-js"))
////                implementation("org.jetbrains.compose.runtime:runtime:1.6.11")
//            }
        }
    }
}
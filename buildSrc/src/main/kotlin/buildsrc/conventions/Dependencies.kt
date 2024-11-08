/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package buildsrc.conventions

val kotlinVersion = "2.0.21"
val coroutinesVersion = "1.8.1"
val serializationVersion = "1.7.1"
val kotestVersion = "5.9.1"
val ktorVersion = "3.0.0"
val composeRuntimeVersion = "1.7.0"

fun kotest(artifact: String): String = "io.kotest:$artifact:$kotestVersion"

fun ktor(artifact: String) = "io.ktor:$artifact:$ktorVersion"

fun coroutines(artifact: String) = "org.jetbrains.kotlinx:$artifact:$coroutinesVersion"

fun serialization(artifact: String) = "org.jetbrains.kotlinx:$artifact:$serializationVersion"

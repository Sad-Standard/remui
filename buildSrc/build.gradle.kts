/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

plugins {
    `kotlin-dsl`
}

dependencies {
    val kotlinVer = "2.0.20"
    implementation(platform(kotlin("bom", kotlinVer)))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVer")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVer")
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:0.4.8")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.9.0")
    implementation("io.kotest:kotest-framework-multiplatform-plugin-gradle:5.9.1")
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.squareup:kotlinpoet-ksp:1.12.0")
}
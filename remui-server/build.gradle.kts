/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

import org.gradle.kotlin.dsl.version

plugins {
    buildsrc.conventions.`kotlin-multiplatform`

}
repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

}


kotlin {

}
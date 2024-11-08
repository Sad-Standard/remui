/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include("remui-compiler-plugin")
include("remui-core")

include("remui-server")
include("remui-client")

include("remui-json")
include("remui-protobuf")

include("remui-ktor-core")
include("remui-ktor-server")


val componentDependencies = mutableMapOf<String, List<String>>()

fun componentSet(
    name: String,
    impls: List<String> = listOf(),
    dependencies: List<String> = listOf()
) {
    fun proj(subName: String, local: List<String> = listOf(), nonLocal: List<String> = listOf()) {
        val fullName = "remui-$name-$subName"
        include(fullName)
        val project = project(":$fullName")
        project.projectDir = file("$rootDir/remui-components/$name/$subName/")

        componentDependencies[fullName] = sequence<String> {
            local.forEach {
                yield("remui-$name-$it")
            }

            dependencies.forEach { name ->
                nonLocal.forEach {
                    yield("remui-$name-$it")
                }
            }

        }.toList()
    }

    proj("set", nonLocal = listOf("set"))
    proj("server", local = listOf("set"), nonLocal = listOf("set", "server"))
    impls.forEach {
        proj("client-$it", local = listOf("set"), nonLocal = listOf("set", "client-$it"))
    }

    // Store dependency information

}

// Define your component sets
componentSet("common", impls = listOf("compose"))
componentSet("html", impls = listOf("compose"), dependencies = listOf("common"))

gradle.extra.set("componentDependencies", componentDependencies)


include("playground-js")
//include("playground-js-server")



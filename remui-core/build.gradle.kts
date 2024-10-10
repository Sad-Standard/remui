/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

//import org.gradle.kotlin.dsl.version
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    buildsrc.conventions.`kotlin-multiplatform`
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("idea")

//    id("io.ktor.plugin") version "3.0.0-rc-1"

}

idea{
    module{
        isDownloadJavadoc = true
        isDownloadSources = true
    }

}
repositories {
    mavenCentral()
    google()

    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

}


kotlin {
//    jvm() {
//        // JVM-specific dependencies
//        withJava()
////        dependencies {
////            implementation(compose)
////        }
//    }
//
//    js(IR) {
//        browser()
//    }
    jvmToolchain(21)

    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.apply{
            add("-Xjvm-default=all")
            add("-Xallow-kotlin-package")
            add("-Xlambdas=class")
            add("-Xcontext-receivers")
        }
    }

    sourceSets {
        fun ktor(artifact: String) = "io.ktor:$artifact:3.0.0-rc-2"
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.compose.runtime:runtime:1.6.11")
//                implementation(kotlin("reflect"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.20")


                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.7.1")
            }
        }
        val jvmMain by getting {
            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
                implementation(ktor("ktor-server-core-jvm"))
                implementation(ktor("ktor-server-netty-jvm"))
                implementation(ktor("ktor-server-cors-jvm"))
                implementation(ktor("ktor-server-websockets-jvm"))
//                implementation("org.jetbrains.compose.runtime:runtime:1.6.11")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.compose.web:web-core:1.6.11")
                implementation(ktor("ktor-client-core"))
                implementation(ktor("ktor-client-js"))
//                implementation("org.jetbrains.compose.runtime:runtime:1.6.11")
            }
        }
    }
}

//val generateImplementations by tasks.registering {
//    doLast {
//        println("Generating implementations")
//        val srcDir = file("src/commonMain/kotlin/")
//
//        val sourcePackageName = "com.gattagdev.remui"
//        val destPackageName = "com.gattagdev.remui.generated" // Define where generated files will go
//        val interfacesToProcess = listOf("BaseElement", "Button") // Add the interface names you want to process
//        val fileSpecBuilder = com.squareup.kotlinpoet.FileSpec.builder("", "GeneratedComponents")
//        interfacesToProcess.forEach { interfaceName ->
//            val className = "${interfaceName}Impl"
//
//            val classBuilder = com.squareup.kotlinpoet.TypeSpec.classBuilder(className)
//                .addModifiers(KModifier.DATA)
//                .addSuperinterface(ClassName(sourcePackageName, interfaceName))
//
//            // For demonstration, hardcode property generation for an interface with `id` and `name`
//            val constructorBuilder = com.squareup.kotlinpoet.FunSpec.constructorBuilder()
//                .addParameter("id"  , Int::class)
//                .addParameter("name", String::class)
//
//            classBuilder.primaryConstructor(constructorBuilder.build())
//
//            classBuilder.addProperty(
//                com.squareup.kotlinpoet.PropertySpec.builder("id", Int::class)
//                    .initializer("id")
//                    .addModifiers(KModifier.OVERRIDE)
//                    .build()
//            )
//
//            classBuilder.addProperty(
//                com.squareup.kotlinpoet.PropertySpec.builder("name", String::class)
//                    .initializer("name")
//                    .addModifiers(KModifier.OVERRIDE)
//                    .build()
//            )
//
//            fileSpecBuilder.addType(classBuilder.build())
//
//
//        }
//        val outputDir = srcDir.resolve(destPackageName.replace('.', '/'))
//        outputDir.mkdirs()
//        fileSpecBuilder.build().writeTo(outputDir)
//    }
//}
//
//tasks.getByName("compileKotlinJvm").dependsOn(generateImplementations)
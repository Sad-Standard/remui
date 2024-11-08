/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

plugins {

}


subprojects {
    group = "com.gattagdev.remui"
    version = "unspecified"
}

allprojects {
//    dependencies {
////        configurations.all {
////            resolutionStrategy.eachDependency{
////                useTarget()
////            }
////        }
//    }
}

//val componentDependencies = gradle.extra["componentDependencies"] as Map<String, List<String>>
//
//subprojects {
//    val projectName = name
//    val componentName = projectName.substringAfter("remui-").substringBefore("-")
//
//    // Get dependencies for this component
//    val dependenciesList = componentDependencies[componentName] ?: emptyList()
//
//    kotlin {
//
//    }
//
////    dependencies {
////        dependenciesList.forEach { depName ->
////            // Replace component name with dependency name in project path
////            val depProjectName = projectName.replace(componentName, depName)
////            implementation(project(":$depProjectName"))
////        }
////    }
//}
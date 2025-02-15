/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.playground


// PLEASE IGNORE THIS FILE, it is purely for testing compiler plugin infra

annotation class InvertedInfix()


//@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Replace(val value: String)

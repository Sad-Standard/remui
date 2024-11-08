/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui.components.html

import org.remui.ClassModule
import org.remui.RemuiConfig
import org.remui.components.common.CommonConfig


val HtmlConfig = CommonConfig + RemuiConfig(
    objectModule = DomComponents + ClassModule(
        Event::class, Event.Other::class,
        Event.Mouse::class, Event.Input::class
    )
)
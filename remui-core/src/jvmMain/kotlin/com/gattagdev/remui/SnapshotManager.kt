/* Copyright Stronghold Robotics, Gregory Tracy, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package com.gattagdev.remui

import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

object GlobalSnapshotManager {
    private val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    private val started = AtomicInteger(0)
    private val sent = AtomicInteger(0)

    fun ensureStarted() {
        if (started.compareAndSet(0, 1)) {
            val channel = Channel<Unit>(1)
            scope.launch() {
                channel.consumeEach {
                    sent.compareAndSet(1, 0)
                    Snapshot.sendApplyNotifications()
                }
            }
            Snapshot.registerGlobalWriteObserver {
                if (sent.compareAndSet(0, 1)) {
                    channel.trySend(Unit)
                }
            }
        }
    }
}
package com.overandoutnerd.deviceinfo.ui.utils

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

suspend fun awaitFrame() {
    delay((1000L / 60L).milliseconds)
}
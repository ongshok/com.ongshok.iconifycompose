package com.ongshok.iconify

import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.engine.HttpClientEngine
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun getHttpClientEngine(): HttpClientEngine = Darwin.create()

actual fun getCurrentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

package com.ongshok.iconify

import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.HttpClientEngine

actual fun getHttpClientEngine(): HttpClientEngine = OkHttp.create()

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

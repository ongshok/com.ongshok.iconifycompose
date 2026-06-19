package com.ongshok.iconify

import io.ktor.client.engine.HttpClientEngine

expect fun getHttpClientEngine(): HttpClientEngine

expect fun getCurrentTimeMillis(): Long

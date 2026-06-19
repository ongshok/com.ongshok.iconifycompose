# Iconify Compose

[![Maven Central](https://img.shields.io/maven-central/v/com.ongshok/iconify)](https://central.sonatype.com/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

An asynchronous icon loading library for **Jetpack Compose** that fetches and renders icons on-the-fly directly from the extensive Iconify API ecosystem.

Designed to keep your APK sizes incredibly small by eliminating static SVG/vector asset overhead, featuring an automated **multi-server fallback architecture** with **circuit-breaking backoff** to ensure zero icon-loading downtime.

---

## Features

- 🚀 **Zero Asset Overhead:** Load any icon dynamically without bundling large vector assets into your production APK.
- 🛡️ **Multi-Server Resiliency:** Specify primary and secondary mirror API servers; if one goes down, the client seamlessly falls back to the next available mirror.
- 🛑 **Smart Backoff (Circuit Breaking):** Failed servers are automatically penalized and put into a cooldown box for 5 minutes, preventing future requests from wasting network time on a degraded host.
- ⚡ **Ultra-Clean Syntax:** Zero boilerplate state-handling inside your UI layer. Simply reference the icon string identifier.
- 🧠 **Global In-Memory Cache:** Automatically caches fetched vectors to ensure lightning-fast UI re-renders and minimal redundant network requests.
- 🔧 **Self-Hosted / Custom Server Support:** Easily configure the library to point to your company's internal or self-hosted Iconify API instances.

---

## Installation

Add the dependency to your library or application module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.ongshok:iconify:1.0.2")
}
```

---

## Setup & Configuration

### Default Usage (Out of the Box)

If you just want to use the public, official Iconify servers, you don't need to configure anything! The library defaults to using *https://api.iconify.design and* *https://api.simplesvg.com* automatically.

### Custom / Self-Hosted Servers

If you host your own internal Iconify API mirror network, initialize the library once inside your Android Application class before rendering any UI:

```kotlin
package com.example.myapp

import android.app.Application
import com.ongshok.iconify.Iconify

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inject your custom primary and fallback server endpoints
        Iconify.initialize(
            customUrls = listOf(
                "https://iconify.mycompany.internal",
                "https://backup-iconify.mycompany.internal",
                "https://api.iconify.design"
            )
        )
    }
}
```

### Usage

Using icons inside your Jetpack Compose screens is incredibly easy and declarative. Simply provide the standard "prefix:name" format string identifier:

```kotlin
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ongshok.iconify.IconifyIcon

@Composable
fun DashboardScreen() {
    // Elegant, single-line asynchronous rendering
    IconifyIcon(
        icon = "lucide:rocket",
        modifier = Modifier.size(64.dp),
        tint = Color.Magenta
    )
}
```

---

## Technical Architecture

The engine works silently beneath the UI layer to maintain structural reliability:

- Request Lifecycle: When IconifyIcon("lucide:rocket") enters composition, it checks the global iconCache. If found, it paints instantly.

- Network Routing: If uncached, a background Coroutine maps the string split into an asynchronous query loop matching only the active healthy server endpoints.

- Failover & Cooldown Box: If an active host throws a network exception, drops a packet, or answers with an HTTP error status code (e.g., 503), the engine catches the exception, logs a timestamp penalty for that specific URL, and automatically advances the payload retrieval to the next backup layout mirror.

- Resilient Recovery: If the entire network connection drops, the library gracefully bypasses individual penalties to ensure structural UI recovery the moment the phone transitions back online.

---

## License

Copyright 2026 Iconify Compose Authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

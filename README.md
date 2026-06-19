# Iconify Compose

[![Maven Central](https://img.shields.io/maven-central/v/com.ongshok/iconify)](https://central.sonatype.com/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

An asynchronous icon loading library for **Compose Multiplatform** that fetches and renders icons on-the-fly directly from the extensive Iconify API ecosystem. 

Supports **Android**, **iOS**, and **Desktop (JVM)**.

Designed to keep your application sizes incredibly small by eliminating static SVG/vector asset overhead, featuring an automated **multi-server fallback architecture** with **circuit-breaking backoff** to ensure zero icon-loading downtime.

---

## Features

- 🚀 **Zero Asset Overhead:** Load any icon dynamically without bundling large vector assets.
- 📱 **Multi-Platform Support:** Seamlessly run on Android, iOS, and Desktop using Compose Multiplatform.
- 🛡️ **Multi-Server Resiliency:** Specify primary and secondary mirror API servers; if one goes down, the client seamlessly falls back to the next available mirror.
- 🛑 **Smart Backoff (Circuit Breaking):** Failed servers are automatically penalized and put into a cooldown box, preventing future requests from wasting network time on a degraded host.
- ⚡ **Ultra-Clean Syntax:** Zero boilerplate state-handling inside your UI layer. Simply reference the icon string identifier.
- 🧠 **Global In-Memory Cache:** Automatically caches fetched vectors to ensure lightning-fast UI re-renders and minimal redundant network requests.
- 🔧 **Self-Hosted / Custom Server Support:** Easily configure the library to point to your company's internal or self-hosted Iconify API instances.

---

## Installation

Add the dependency to your shared module's `commonMain` dependencies in `build.gradle.kts`:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.ongshok:iconify:1.0.4")
        }
    }
}
```

---

## Usage

Using icons inside your Compose screens is incredibly easy and declarative. Simply provide the standard "prefix:name" format string identifier (e.g., `lucide:smile`):

```kotlin
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ongshok.iconify.ui.IconifyIcon

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

## Setup & Configuration

### Default Usage (Out of the Box)

If you just want to use the public, official Iconify servers, you don't need to configure anything! The library defaults to using *https://api.iconify.design and* *https://api.simplesvg.com* automatically.

### Custom / Self-Hosted Servers

If you host your own internal Iconify API mirror network, initialize the library once at application startup before rendering any UI:

```kotlin
// In your Android Application class, iOS Main entry, or Desktop main()
Iconify.initialize(
    customUrls = listOf(
        "https://iconify.mycompany.internal",
        "https://api.iconify.design"
    )
)
```

---

## Development & Testing

### Desktop Demo App
To run the desktop demo app and see the icons in action:
```bash
./gradlew :app:run
```

### Run Library Tests
To run the JVM-based tests for the library:
```bash
./gradlew :library:jvmTest
```

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

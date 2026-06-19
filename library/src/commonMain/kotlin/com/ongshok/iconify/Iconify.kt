package com.ongshok.iconify

import com.ongshok.iconify.data.IconifyClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object Iconify {
    private val defaultHttpClient = HttpClient(getHttpClientEngine()) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // Use a mutable variable (var) internal to the library so it can be re-initialized
    internal var sharedClient = IconifyClient(
        serverUrls = listOf("https://api.iconify.design", "https://api.simplesvg.com"),
        httpClient = defaultHttpClient
    )

    /**
     * Call this at application startup to configure your own self-hosted Iconify servers.
     * @param customUrls List of your custom Iconify server base URLs (e.g., "https://iconify.mycompany.com")
     */
    fun initialize(customUrls: List<String>) {
        require(customUrls.isNotEmpty()) { "Server URL list cannot be empty" }

        sharedClient = IconifyClient(
            serverUrls = customUrls,
            httpClient = defaultHttpClient
        )
    }
}
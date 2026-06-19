package com.ongshok.iconify.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object IconifyClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    private val serverUrls: List<String> = listOf("https://api.iconify.design", "https://api.simplesvg.com")

    /**
     * Fetches specific icon details from Iconify API
     * @param component e.g., "mdi:home" splits into prefix = "mdi", name = "home"
     */
    suspend fun fetchIcon(component: String): IconData? {
        val parts = component.split(":")
        if (parts.size != 2) return null

        val prefix = parts[0]
        val iconName = parts[1]

        for (baseUrl in serverUrls) {
            try {
                val response: IconifyResponse = client.get("$baseUrl/$prefix.json") {
                    parameter("icons", iconName)
                }.body()

                return response.icons[iconName]
            } catch (e: Exception) {
                e.printStackTrace()
                // Log exception or silently fall back to the next server mirror
//                println("Server $baseUrl failed, trying fallback mirror...")
            }
        }
        return null // All servers failed
    }
}
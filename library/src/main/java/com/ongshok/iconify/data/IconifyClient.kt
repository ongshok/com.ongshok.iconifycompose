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

    private const val BASE_URL = "https://api.iconify.design"

    /**
     * Fetches specific icon details from Iconify API
     * @param component e.g., "mdi:home" splits into prefix = "mdi", name = "home"
     */
    suspend fun fetchIcon(component: String): IconData? {
        val parts = component.split(":")
        if (parts.size != 2) return null

        val prefix = parts[0]
        val iconName = parts[1]

        return try {
            val response: IconifyResponse = client.get("$BASE_URL/$prefix.json") {
                parameter("icons", iconName)
            }.body()

            response.icons[iconName]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
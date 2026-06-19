package com.ongshok.iconify.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

class IconifyClient(
    // Accept a list of servers, defaulting to your primary ones
    private val serverUrls: List<String> = listOf("https://api.iconify.design", "https://api.simplesvg.com"),
    private val httpClient: HttpClient = HttpClient(),
    private val backoffDurationMs: Long = 5.minutes.inWholeMilliseconds // Cooldown period
) {
    // Thread-safe map to store the timestamp (epoch ms) when a server failed
    private val failedServers = ConcurrentHashMap<String, Long>()

    suspend fun fetchIcon(component: String): IconData? {
        val currentTime = System.currentTimeMillis()
        val parts = component.split(":")

        var prefix = parts[0]
        var iconName = parts[1]
        if (parts.size != 2) {
            prefix = "lucide"
            iconName = parts[0]
        }

        // 1. Filter out servers that are currently in the backoff penalty box
        val activeServers = serverUrls.filter { url ->
            val failureTime = failedServers[url]
            if (failureTime == null) {
                true // Server has no recorded failures
            } else if (currentTime - failureTime > backoffDurationMs) {
                failedServers.remove(url) // Backoff expired! Forgive the server
                true
            } else {
                false // Server is still backing off, skip it
            }
        }

        // 2. Fallback strategy: If ALL servers are backing off, reuse all of them as a last resort
        val serversToTry = activeServers.ifEmpty { serverUrls }

        for (baseUrl in serversToTry) {
            try {
                val response = httpClient.get("$baseUrl/$prefix.json?icons=$iconName")
                if (response.status.value == 200) {

                    // Deserialize the full API wrapper envelope
                    val apiResponse = response.body<IconifyResponse>()

                    // Extract and return your exact targeted IconData object from the map
                    return apiResponse.icons[iconName]
                } else {
                    markAsFailed(baseUrl, currentTime)
                }
            } catch (_: Exception) {
                markAsFailed(baseUrl, currentTime)
                // Log exception or silently fall back to the next server mirror
                println("Server $baseUrl failed. Backing off for ${backoffDurationMs / 1000}s.")
            }
        }
        return null // All servers failed
    }

    private fun markAsFailed(url: String, timestamp: Long) {
        failedServers[url] = timestamp
    }
}
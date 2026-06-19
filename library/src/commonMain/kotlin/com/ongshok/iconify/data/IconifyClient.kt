package com.ongshok.iconify.data

import com.ongshok.iconify.getCurrentTimeMillis
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.minutes

class IconifyClient(
    // Accept a list of servers, defaulting to your primary ones
    private val serverUrls: List<String> = listOf("https://api.iconify.design", "https://api.simplesvg.com"),
    private val httpClient: HttpClient = HttpClient(),
    private val backoffDurationMs: Long = 5.minutes.inWholeMilliseconds // Cooldown period
) {
    // Thread-safe map to store the timestamp (epoch ms) when a server failed
    private val failedServers = mutableMapOf<String, Long>()
    private val mapMutex = Mutex()

    suspend fun fetchIcon(component: String): IconData? {
        val parts = component.split(":")
        val prefix: String
        val iconName: String

        if (parts.size >= 2) {
            prefix = parts[0]
            iconName = parts.drop(1).joinToString(":") // Handles names that might contain internal colons safely
        } else if (parts.isNotEmpty() && parts[0].isNotBlank()) {
            prefix = "lucide"
            iconName = parts[0]
        } else {
            return null // Invalid component identifier string
        }

        val currentTime = getCurrentTimeMillis()

        // 1. Filter out servers that are currently in the backoff penalty box
        val activeServers = mapMutex.withLock {
            serverUrls.filter { url ->
                val failureTime = failedServers[url]
                if (failureTime == null) {
                    true
                } else if (currentTime - failureTime > backoffDurationMs) {
                    failedServers.remove(url) // Forgive the server safely
                    true
                } else {
                    false
                }
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

                    //  the true key name (resolve aliases if they exist)
                    val trueIconName = apiResponse.aliases?.get(iconName)?.parent ?: iconName

                    // Extract and return your exact targeted IconData object from the map
                    return apiResponse.icons[trueIconName]
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

    private suspend fun markAsFailed(url: String, timestamp: Long) {
        mapMutex.withLock {
            failedServers[url] = timestamp
        }
    }
}
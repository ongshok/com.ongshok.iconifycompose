package com.ongshok.iconify.cache

import androidx.compose.ui.graphics.vector.ImageVector

object IconCache {
    private val cache = mutableMapOf<String, ImageVector>()

    /**
     * Retrieves an icon from memory if it exists.
     */
    fun get(key: String): ImageVector? = cache[key]

    /**
     * Stores a parsed icon into memory.
     */
    fun put(key: String, vector: ImageVector) {
        cache[key] = vector
    }

    /**
     * Clears the cache if needed (e.g., low memory events)
     */
    fun clear() {
        cache.clear()
    }
}
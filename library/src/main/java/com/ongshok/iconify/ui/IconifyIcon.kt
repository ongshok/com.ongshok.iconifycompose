package com.ongshok.iconify.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ongshok.iconify.cache.IconCache
import com.ongshok.iconify.data.IconifyClient
import com.ongshok.iconify.parser.SvgPathParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun IconifyIcon(
    icon: String, // format: "prefix:name" (e.g., "lucide:smile")
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    placeholder: @Composable () -> Unit = { Box(modifier.size(24.dp)) }
) {
    val currentContentColor = LocalContentColor.current
    val targetTint = if (tint == Color.Unspecified) currentContentColor else tint

    val initialCachedVector = IconCache.get(icon)

    val vectorState = produceState(initialValue = initialCachedVector, key1 = icon) {
        // If it was already loaded from cache into initialValue, we can stop here
        if (value != null) return@produceState

        value = withContext(Dispatchers.IO) {
            // Double-check inside the background thread to prevent duplicate network calls
            IconCache.get(icon)?.let { return@withContext it }

            val iconData = IconifyClient.fetchIcon(icon)
            Log.d("IconifyIcon", "iconData: $iconData")
            if (iconData != null) {
                val parsedVector = SvgPathParser.createVector(iconData, name = icon)

                // Save it to memory for the next time it's requested!
                if (parsedVector != null) {
                    IconCache.put(icon, parsedVector)
                }

                parsedVector
            } else {
                null
            }
        }
    }

    val imageVector = vectorState.value

    if (imageVector != null) {
        Icon(
            imageVector = imageVector,
            contentDescription = icon,
            modifier = modifier,
            tint = targetTint
        )
    } else {
        placeholder()
    }
}
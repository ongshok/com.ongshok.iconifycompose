package com.ongshok.iconify.parser

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import com.ongshok.iconify.data.IconData

object SvgPathParser {

    /**
     * Converts raw Iconify IconData into an Android Jetpack Compose ImageVector
     */
    fun createVector(iconData: IconData, name: String): ImageVector? {
        val pathStrings = extractAllPathData(iconData.body)

        // Default Iconify viewports are typically 24x24 unless specified
        val viewportWidth = iconData.width?.toFloat() ?: 24f
        val viewportHeight = iconData.height?.toFloat() ?: 24f

        return try {
            ImageVector.Builder(
                name = name,
                defaultWidth = viewportWidth.dp,
                defaultHeight = viewportHeight.dp,
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight
            ).apply {
                // Loop through and add every single path match found in the SVG body
                pathStrings.forEach { pathString ->
                    val pathNodes = addPathNodes(pathString)
                    addPath(
                        pathData = pathNodes,
                        // CRITICAL: Must provide a base solid fill (e.g., Black)
                        // so the Icon composable's tint can blend over it.
                        fill = SolidColor(Color.Black)
                    )
                }
            }.build()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Extracts all 'd' attribute values out of the SVG string, supporting multi-path SVGs
     */
    private fun extractAllPathData(svgBody: String): List<String> {
        val regex = "d=\"([^\"]+)\"".toRegex()
        return regex.findAll(svgBody).map { it.groupValues[1] }.toList()
    }
}
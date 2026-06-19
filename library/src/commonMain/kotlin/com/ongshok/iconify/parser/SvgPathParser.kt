package com.ongshok.iconify.parser

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import com.ongshok.iconify.data.IconData

//object SvgPathParser {
//
//    /**
//     * Converts raw Iconify IconData into an Android Jetpack Compose ImageVector
//     */
//    fun createVector(iconData: IconData, name: String): ImageVector? {
//        val pathStrings = extractAllPathData(iconData.body)
//
//        // Default Iconify viewports are typically 24x24 unless specified
//        val viewportWidth = iconData.width?.toFloat() ?: 24f
//        val viewportHeight = iconData.height?.toFloat() ?: 24f
//
//        return try {
//            ImageVector.Builder(
//                name = name,
//                defaultWidth = viewportWidth.dp,
//                defaultHeight = viewportHeight.dp,
//                viewportWidth = viewportWidth,
//                viewportHeight = viewportHeight
//            ).apply {
//                // Loop through and add every single path match found in the SVG body
//                pathStrings.forEach { pathString ->
//                    val pathNodes = addPathNodes(pathString)
//                    addPath(
//                        pathData = pathNodes,
//                        // CRITICAL: Must provide a base solid fill (e.g., Black)
//                        // so the Icon composable's tint can blend over it.
//                        fill = SolidColor(Color.Black)
//                    )
//                }
//            }.build()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    /**
//     * Extracts all 'd' attribute values out of the SVG string, supporting multi-path SVGs
//     */
//    private fun extractAllPathData(svgBody: String): List<String> {
//        val regex = "d=\"([^\"]+)\"".toRegex()
//        return regex.findAll(svgBody).map { it.groupValues[1] }.toList()
//    }
//}

object SvgPathParser {

    private data class ExtractedElement(
        val pathNodesStr: String,
        val isStrokeBased: Boolean,
        val strokeWidth: Float,
        val strokeCap: StrokeCap,
        val strokeJoin: StrokeJoin
    )

    fun createVector(iconData: IconData, name: String): ImageVector? {
        val parsedElements = parseSvgElements(iconData.body)

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
                parsedElements.forEach { element ->
                    val nodes = addPathNodes(element.pathNodesStr)

                    if (element.isStrokeBased) {
                        addPath(
                            pathData = nodes,
                            stroke = SolidColor(Color.Black),
                            strokeLineWidth = element.strokeWidth,
                            strokeLineCap = element.strokeCap,
                            strokeLineJoin = element.strokeJoin
                        )
                    } else {
                        addPath(
                            pathData = nodes,
                            fill = SolidColor(Color.Black)
                        )
                    }
                }
            }.build()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseSvgElements(svgBody: String): List<ExtractedElement> {
        val elements = mutableListOf<ExtractedElement>()

        // Extract global wrapping styles
        val globalFillNone = svgBody.contains("fill=\"none\"") || svgBody.contains("fill='none'")
        val globalStrokeWidth = extractAttribute(svgBody, "stroke-width")?.toFloatOrNull() ?: 2f
        val globalStrokeCap = parseStrokeCap(extractAttribute(svgBody, "stroke-linecap"))
        val globalStrokeJoin = parseStrokeJoin(extractAttribute(svgBody, "stroke-linejoin"))

        // Catch all structural shape tags
        val tagRegex = "<(path|rect|circle|ellipse|line|polyline|polygon)[^>]*>".toRegex()
        val matches = tagRegex.findAll(svgBody)

        for (match in matches) {
            val fullTag = match.value
            val tagName = match.groupValues[1]

            // Convert any primitive shape type directly into uniform path data coordinates
            val pathDataStr = when (tagName) {
                "path"     -> extractAttribute(fullTag, "d")
                "rect"     -> convertRectToPathData(fullTag)
                "circle"   -> convertCircleToPathData(fullTag)
                "ellipse"  -> convertEllipseToPathData(fullTag)
                "line"     -> convertLineToPathData(fullTag)
                "polyline" -> convertPointsToPathData(fullTag, closePath = false)
                "polygon"  -> convertPointsToPathData(fullTag, closePath = true)
                else       -> null
            } ?: continue

            val localFill = extractAttribute(fullTag, "fill")
            val localStroke = extractAttribute(fullTag, "stroke")
            val localStrokeWidth = extractAttribute(fullTag, "stroke-width")?.toFloatOrNull()

            val isStrokeBased = when {
                localStroke != null && localStroke != "none" -> true
                localFill == "none" -> true
                tagName == "line" || tagName == "polyline" -> true // Lines default to stroke-based
                globalFillNone && (localFill == null || localFill == "none") -> true
                else -> false
            }

            elements.add(
                ExtractedElement(
                    pathNodesStr = pathDataStr,
                    isStrokeBased = isStrokeBased,
                    strokeWidth = localStrokeWidth ?: globalStrokeWidth,
                    strokeCap = parseStrokeCap(extractAttribute(fullTag, "stroke-linecap")) ?: globalStrokeCap ?: StrokeCap.Round,
                    strokeJoin = parseStrokeJoin(extractAttribute(fullTag, "stroke-linejoin")) ?: globalStrokeJoin ?: StrokeJoin.Round
                )
            )
        }

        return elements
    }

    private fun convertRectToPathData(rectTag: String): String? {
        val x = extractAttribute(rectTag, "x")?.toFloatOrNull() ?: 0f
        val y = extractAttribute(rectTag, "y")?.toFloatOrNull() ?: 0f
        val width = extractAttribute(rectTag, "width")?.toFloatOrNull() ?: return null
        val height = extractAttribute(rectTag, "height")?.toFloatOrNull() ?: return null
        val rx = extractAttribute(rectTag, "rx")?.toFloatOrNull()
        val ry = extractAttribute(rectTag, "ry")?.toFloatOrNull() ?: rx

        return if (rx != null && rx > 0f) {
            val w = width
            val h = height
            val r = rx.coerceAtMost(w / 2f).coerceAtMost(h / 2f)
            "M ${x + r} $y h ${w - 2 * r} a $r $r 0 0 1 $r $r v ${h - 2 * r} a $r $r 0 0 1 -$r $r h -${w - 2 * r} a $r $r 0 0 1 -$r -$r v -${h - 2 * r} a $r $r 0 0 1 $r -$r Z"
        } else {
            "M $x $y h $width v $height h -$width Z"
        }
    }

    private fun convertCircleToPathData(circleTag: String): String? {
        val cx = extractAttribute(circleTag, "cx")?.toFloatOrNull() ?: 0f
        val cy = extractAttribute(circleTag, "cy")?.toFloatOrNull() ?: 0f
        val r = extractAttribute(circleTag, "r")?.toFloatOrNull() ?: return null
        // Form a circle using two sequential 180-degree sweep arc commands
        return "M ${cx - r} $cy a $r $r 0 1 0 ${2 * r} 0 a $r $r 0 1 0 -${2 * r} 0 Z"
    }

    private fun convertEllipseToPathData(ellipseTag: String): String? {
        val cx = extractAttribute(ellipseTag, "cx")?.toFloatOrNull() ?: 0f
        val cy = extractAttribute(ellipseTag, "cy")?.toFloatOrNull() ?: 0f
        val rx = extractAttribute(ellipseTag, "rx")?.toFloatOrNull() ?: return null
        val ry = extractAttribute(ellipseTag, "ry")?.toFloatOrNull() ?: return null
        // Form an ellipse using two sequential sweep arc commands using distinct radii (rx, ry)
        return "M ${cx - rx} $cy a $rx $ry 0 1 0 ${2 * rx} 0 a $rx $ry 0 1 0 -${2 * rx} 0 Z"
    }

    private fun convertLineToPathData(lineTag: String): String? {
        val x1 = extractAttribute(lineTag, "x1")?.toFloatOrNull() ?: 0f
        val y1 = extractAttribute(lineTag, "y1")?.toFloatOrNull() ?: 0f
        val x2 = extractAttribute(lineTag, "x2")?.toFloatOrNull() ?: 0f
        val y2 = extractAttribute(lineTag, "y2")?.toFloatOrNull() ?: 0f
        return "M $x1 $y1 L $x2 $y2"
    }

    private fun convertPointsToPathData(tag: String, closePath: Boolean): String? {
        val pointsStr = extractAttribute(tag, "points")?.trim() ?: return null
        // Split by whitespace or commas to isolate individual numbers
        val points = pointsStr.split("[\\s,]+".toRegex()).filter { it.isNotEmpty() }
        if (points.size < 4 || points.size % 2 != 0) return null

        val sb = StringBuilder()
        sb.append("M ").append(points[0]).append(" ").append(points[1])

        for (i in 2 until points.size step 2) {
            sb.append(" L ").append(points[i]).append(" ").append(points[i + 1])
        }

        if (closePath) sb.append(" Z")
        return sb.toString()
    }

    private fun extractAttribute(element: String, attribute: String): String? {
        val regex = "$attribute=[\"']([^\"']+)[\"']".toRegex()
        return regex.find(element)?.groupValues?.get(1)
    }

    private fun parseStrokeCap(value: String?): StrokeCap? = when (value) {
        "butt" -> StrokeCap.Butt
        "square" -> StrokeCap.Square
        "round" -> StrokeCap.Round
        else -> null
    }

    private fun parseStrokeJoin(value: String?): StrokeJoin? = when (value) {
        "miter" -> StrokeJoin.Miter
        "bevel" -> StrokeJoin.Bevel
        "round" -> StrokeJoin.Round
        else -> null
    }
}
package com.ongshok.iconify.data

import kotlinx.serialization.Serializable

@Serializable
data class IconifyResponse(
    val prefix: String,
    val icons: Map<String, IconData>,
    val aliases: Map<String, IconifyAlias>? = null,
    val width: Int? = null,
    val height: Int? = null
)

@Serializable
data class IconifyAlias(
    val parent: String
)

@Serializable
data class IconData(
    val body: String, // This contains the raw internal SVG tags (e.g., "<path d='...'/>")
    val width: Int? = null,
    val height: Int? = null
)
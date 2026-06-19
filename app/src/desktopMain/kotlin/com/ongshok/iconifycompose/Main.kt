package com.ongshok.iconifycompose

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Iconify Compose Desktop") {
        App()
    }
}

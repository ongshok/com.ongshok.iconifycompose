package com.ongshok.iconifycompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ongshok.iconify.ui.IconifyIcon
import com.ongshok.iconifycompose.ui.theme.IconifyComposeTheme

@Composable
fun App() {
    IconifyComposeTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pulls cleanly from Iconify library dynamically!
            IconifyIcon(
                icon = "lucide:rocket",
                modifier = Modifier.size(64.dp),
                tint = Color.Magenta
            )

            IconifyIcon(
                icon = "mdi:fire",
                modifier = Modifier.size(64.dp),
                tint = Color.Red
            )
        }
    }
}

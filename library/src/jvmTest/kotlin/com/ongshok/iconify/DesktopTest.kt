package com.ongshok.iconify

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import com.ongshok.iconify.ui.IconifyIcon
import org.junit.Rule
import org.junit.Test

class DesktopTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testIconDisplay() {
        composeTestRule.setContent {
            IconifyIcon(icon = "lucide:smile")
        }
        
        // Wait for the icon to load (it's asynchronous)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("lucide:smile").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("lucide:smile").assertExists()
    }
}

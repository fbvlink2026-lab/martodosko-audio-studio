package com.martodosko.studio.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Enum class representing the main tabs in the app.
 */
enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Default.Home),
    MIXER("mixer", "Mixer", Icons.Default.GraphicEq),
    PRESETS("presets", "Presets", Icons.Default.QueueMusic),
    SETTINGS("settings", "Settings", Icons.Default.Settings)
}

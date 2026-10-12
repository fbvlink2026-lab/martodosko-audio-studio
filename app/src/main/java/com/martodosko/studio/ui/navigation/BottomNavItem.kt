// ==================================================
// FILE: BottomNavItem.kt
// VERSION: 2.1.0 - FIXED ICONS
// PURPOSE: Define Main Tabs using Standard Material Icons
// NOTE: Replaced Extended Icons with Core Icons to avoid missing dependency errors
// ==================================================
package com.martodosko.studio.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List // Used for Presets/Library
import androidx.compose.material.icons.filled.Tune   // Used for Mixer/Controls
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
    MIXER("mixer", "Mixer", Icons.Default.Tune),      // Changed from GraphicEq
    PRESETS("presets", "Presets", Icons.Default.List), // Changed from QueueMusic
    SETTINGS("settings", "Settings", Icons.Default.Settings)
}

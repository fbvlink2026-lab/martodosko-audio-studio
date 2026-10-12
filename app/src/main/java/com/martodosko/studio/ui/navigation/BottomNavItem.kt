// ==================================================
// FILE: BottomNavItem.kt
// VERSION: 2.1.0 - FINAL CORE ICONS FIX
// PURPOSE: Define Main Tabs using ONLY Standard Material Icons
// NOTE: Removed 'Tune' and 'QueueMusic' as they require extended lib
// ==================================================
package com.martodosko.studio.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List   // ✅ Available in Core (For Presets)
import androidx.compose.material.icons.filled.Menu   // ✅ Available in Core (For Mixer/Hamburger style)
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
    MIXER("mixer", "Mixer", Icons.Default.Menu),      // Changed to Menu (Core Icon)
    PRESETS("presets", "Presets", Icons.Default.List), // Changed to List (Core Icon)
    SETTINGS("settings", "Settings", Icons.Default.Settings)
}

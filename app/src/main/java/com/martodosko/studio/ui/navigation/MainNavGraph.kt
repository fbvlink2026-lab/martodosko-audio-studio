// ==================================================
// FILE: MainNavGraph.kt
// VERSION: v2.3.0-phase1-step4
// PURPOSE: Central Navigation Definition & Bottom Bar Logic
// NOTE: Extracted from MainActivity for separation of concerns.
// ==================================================
package com.martodosko.studio.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.martodosko.studio.ui.screens.home.PedalBoardScreen

/**
 * Data class representing a single destination in the bottom navigation.
 */
data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/**
 * The list of all destinations for the main app shell.
 */
val navItems = listOf(
    NavItem("mixer", "Mixer", Icons.Default.Tune),      
    NavItem("presets", "Presets", Icons.Default.List),   
    NavItem("home", "Board", Icons.Default.GridView),    
    NavItem("settings", "Settings", Icons.Default.Settings), 
    NavItem("more", "More", Icons.Default.Menu)          
)

@Composable
fun MainNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { 
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home", // Default to Pedal Board (Center)
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { PedalBoardScreen() } 
            composable("mixer") { PlaceholderScreen("🎚️ Mixer Monitor") }
            composable("presets") { PlaceholderScreen("📋 Preset Library") }
            composable("settings") { PlaceholderScreen("⚙️ Settings") }
            composable("more") { PlaceholderScreen("☰ More Menu") }
        }
    }
}

// Helper for placeholders within the graph
@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color(0xFF40E0D0), fontSize = 24.sp)
    }
}

// ==================================================
// FILE: MainActivity.kt
// VERSION: 2.1.0 - FINAL CLEAN BUILD
// PURPOSE: Host Activity with Bottom Nav Bar
// NOTE: Uses Enum Icons and Inline NavGraph to avoid missing files
// ==================================================
package com.martodosko.studio.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.martodosko.studio.ui.screens.home.PedalBoardScreen
import com.martodosko.studio.ui.screens.placeholder.PlaceholderScreen
import com.martodosko.studio.ui.theme.MartodoskoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MartodoskoTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { 
                        MainBottomNavBar(navController = navController) 
                    }
                ) { innerPadding ->
                    // Content Area
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { PedalBoardScreen() }
                        composable("mixer") { PlaceholderScreen(title = "🎚️ Mixer Channel") }
                        composable("presets") { PlaceholderScreen(title = "📋 Preset Library") }
                        composable("settings") { PlaceholderScreen(title = "⚙️ Settings") }
                    }
                }
            }
        }
    }
}

/**
 * Data Class for Navigation Items (Defined locally to ensure availability)
 */
data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val navItems = listOf(
    NavItem("home", "Home", Icons.Default.Home),
    NavItem("mixer", "Mixer", Icons.Default.Menu),      // Using Menu as placeholder for Mixer
    NavItem("presets", "Presets", Icons.Default.List),  // Using List for Presets
    NavItem("settings", "Settings", Icons.Default.Settings)
)

@Composable
fun MainBottomNavBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
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

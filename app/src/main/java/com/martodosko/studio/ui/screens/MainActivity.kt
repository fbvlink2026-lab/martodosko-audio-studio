// ==================================================
// FILE: MainActivity.kt
// VERSION: 2.1.0 - BOTTOM NAVIGATION IMPLEMENTATION
// PURPOSE: Host Activity with Bottom Nav Bar
// ==================================================
package com.martodosko.studio.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.martodosko.studio.ui.navigation.MainNavGraph // We will create this next
import com.martodosko.studio.ui.theme.MartodoskoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MartodoskoTheme {
                // Remember NavController to manage screen transitions
                val navController = rememberNavController()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { 
                        // The Bottom Navigation Bar
                        MainBottomNavBar(navController = navController) 
                    }
                ) { innerPadding ->
                    // The Content Area (Screens)
                    // Apply padding so content doesn't hide behind the bar
                    MainNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Composable for the Bottom Navigation Bar itself.
 */
@Composable
fun MainBottomNavBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        // Iterate through our enum items
        listOf(
            com.martodosko.studio.ui.navigation.BottomNavItem.HOME,
            com.martodosko.studio.ui.navigation.BottomNavItem.MIXER,
            com.martodosko.studio.ui.navigation.BottomNavItem.PRESETS,
            com.martodosko.studio.ui.navigation.BottomNavItem.SETTINGS
        ).forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    // Navigate to the new route
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to avoid building up a large stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }
    }
}

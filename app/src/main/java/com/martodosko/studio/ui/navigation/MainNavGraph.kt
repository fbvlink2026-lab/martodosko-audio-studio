package com.martodosko.studio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.martodosko.studio.ui.screens.home.PedalBoardScreen
import com.martodosko.studio.ui.screens.placeholder.PlaceholderScreen // Create simple placeholders for now

@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.HOME.route,
        modifier = modifier
    ) {
        // HOME TAB
        composable(BottomNavItem.HOME.route) {
            PedalBoardScreen() // Our existing Pedal Board
        }

        // MIXER TAB
        composable(BottomNavItem.MIXER.route) {
            PlaceholderScreen(title = "🎚️ Mixer Channel")
        }

        // PRESETS TAB
        composable(BottomNavItem.PRESETS.route) {
            PlaceholderScreen(title = "📋 Preset Library")
        }

        // SETTINGS TAB
        composable(BottomNavItem.SETTINGS.route) {
            PlaceholderScreen(title = "⚙️ Settings")
        }
    }
}

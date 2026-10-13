// ==================================================
// FILE: MainActivity.kt
// VERSION: v2.3.0-phase1-step1-clean
// PURPOSE: Host Activity using Modular Components
// NOTE: Verified clean syntax. Imports at top only.
// ==================================================
package com.martodosko.studio.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.martodosko.studio.ui.components.ProAudioKnob
import com.martodosko.studio.ui.theme.MartodoskoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("MAIN_ACT", "✅ Starting Refactored Main Activity (Step 1.1)")

        setContent {
            MartodoskoTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SimpleSplashScreen(onFinished = { showSplash = false })
                } else {
                    MainAppShell()
                }
            }
        }
    }
}

// --- SPLASH (Still Inline for Safety in Step 1) ---
@Composable
fun SimpleSplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1000L)
        onFinished()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("MARTODOSKO", color = Color(0xFF40E0D0), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator(color = Color(0xFF40E0D0))
        }
    }
}

// --- NAVIGATION SHELL ---
data class NavItem(val route: String, val label: String, val icon: ImageVector)

val navItems = listOf(
    NavItem("mixer", "Mixer", Icons.Default.Tune),      
    NavItem("presets", "Presets", Icons.Default.List),   
    NavItem("home", "Board", Icons.Default.GridView),    
    NavItem("settings", "Settings", Icons.Default.Settings), 
    NavItem("more", "More", Icons.Default.Menu)          
)

@Composable
fun MainAppShell() {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
            composable("home") { HomeScreenContent() }
            composable("mixer") { PlaceholderScreen("🎚️ Mixer Monitor") }
            composable("presets") { PlaceholderScreen("📋 Preset Library") }
            composable("settings") { PlaceholderScreen("⚙️ Settings") }
            composable("more") { PlaceholderScreen("☰ More Menu") }
        }
    }
}

// --- HOME CONTENT (Using Imported Knob) ---
@Composable
fun HomeScreenContent() {
    var vocalGain by remember { mutableStateOf(0f) }
    var guitarDrive by remember { mutableStateOf(0f) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionHeader("🎤 VOCAL CHAIN", Color.Cyan)
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                ProAudioKnob(
                    value = vocalGain,
                    onValueChange = { vocalGain = it },
                    label = "GAIN",
                    minValue = -50f,
                    maxValue = 50f,
                    knobSize = 100.dp,
                    accentColor = Color(0xFF40E0D0)
                )
            }
        }
        item {
            SectionHeader("🎸 GUITAR CHAIN", Color(0xFFFF9800))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                ProAudioKnob(
                    value = guitarDrive,
                    onValueChange = { guitarDrive = it },
                    label = "DRIVE",
                    minValue = -50f,
                    maxValue = 50f,
                    knobSize = 100.dp,
                    accentColor = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, color: Color) {
    Text(text = title, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color(0xFF40E0D0), fontSize = 24.sp)
    }
}

// ==================================================
// FILE: MainActivity.kt
// VERSION: v2.3.0-phase1-step1-refactored
// PURPOSE: Host Activity using Modular Components
// NOTE: Imports ProAudioKnob instead of defining it inline
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
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
import com.martodosko.studio.ui.components.ProAudioKnob // ✅ IMPORT NEW COMPONENT
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
    NavItem("mixer", "Mixer", Icons.Default.Tune),      // Tab 1: Far Left
    NavItem("presets", "Presets", Icons.Default.List),   // Tab 2: Mid-Left
    NavItem("home", "Board", Icons.Default.GridView),    // Tab 3: CENTER (Neutral Icon)
    NavItem("settings", "Settings", Icons.Default.Settings), // Tab 4: Mid-Right
    NavItem("more", "More", Icons.Default.Menu)          // Tab 5: Far Right
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
                ProAudioKnob( // ✅ USING EXTERNAL COMPONENT
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
                ProAudioKnob( // ✅ USING EXTERNAL COMPONENT
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
}// ==================================================
// FILE: MainActivity.kt
// VERSION: v2.1.0-monolith-final-fixed-imports
// PURPOSE: Consolidated App Shell (Splash + Nav + UI)
// NOTE: Added missing 'lazy' imports for LazyColumn
// ==================================================
package com.martodosko.studio.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // ✅ CRITICAL IMPORT ADDED HERE
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.martodosko.studio.ui.theme.MartodoskoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.e("MONOLITH", "✅ MainActivity Started")

        setContent {
            MartodoskoTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    MonolithSplashScreen(
                        onFinished = { 
                            Log.e("MONOLITH", "➡️ Transitioning to Main")
                            showSplash = false 
                        }
                    )
                } else {
                    MonolithMainApp()
                }
            }
        }
    }
}

// ==========================================
// COMPONENT 1: SPLASH SCREEN
// ==========================================
@Composable
fun MonolithSplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500L)
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
            Spacer(modifier = Modifier.height(10.dp))
            Text("Loading...", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

// ==========================================
// COMPONENT 2: MAIN APP SHELL
// ==========================================
data class NavItem(val route: String, val label: String, val icon: ImageVector)

val navItems = listOf(
    NavItem("home", "Home", Icons.Default.Home),
    NavItem("mixer", "Mixer", Icons.Default.Menu),
    NavItem("presets", "Presets", Icons.Default.List),
    NavItem("settings", "Settings", Icons.Default.Settings)
)

@Composable
fun MonolithMainApp() {
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
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { MonolithPedalBoard() }
            composable("mixer") { SimpleTextScreen("🎚️ Mixer Channel") }
            composable("presets") { SimpleTextScreen("📋 Preset Library") }
            composable("settings") { SimpleTextScreen("⚙️ Settings") }
        }
    }
}

// ==========================================
// COMPONENT 3: PLACEHOLDER
// ==========================================
@Composable
fun SimpleTextScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color(0xFF40E0D0), fontSize = 24.sp)
    }
}

// ==========================================
// COMPONENT 4: PEDAL BOARD
// ==========================================
@Composable
fun MonolithPedalBoard() {
    var vocalGain by remember { mutableStateOf(0f) }
    var vocalVol by remember { mutableStateOf(80f) }
    var guitarDrive by remember { mutableStateOf(0f) }
    var guitarLevel by remember { mutableStateOf(75f) }

    // ✅ LazyColumn is now recognized because of the import above
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionHeader("🎤 VOCAL CHANNEL", Color(0xFF40E0D0))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InlineKnob(value = vocalGain, onChange = { vocalGain = it }, label = "GAIN", min = -50f, max = 50f, accent = Color(0xFF40E0D0))
                InlineKnob(value = vocalVol, onChange = { vocalVol = it }, label = "VOL", min = 0f, max = 100f, accent = Color.White)
            }
        }

        item { Divider(color = Color(0xFF12232E), thickness = 2.dp) }

        item {
            SectionHeader("🎸 GUITAR EFFECTS", Color(0xFFFF9800))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InlineKnob(value = guitarDrive, onChange = { guitarDrive = it }, label = "DRIVE", min = -50f, max = 50f, accent = Color(0xFFFF9800))
                InlineKnob(value = guitarLevel, onChange = { guitarLevel = it }, label = "LEVEL", min = 0f, max = 100f, accent = Color.White)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, color: Color) {
    Text(text = title, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
}

// ==========================================
// COMPONENT 5: INLINE KNOB
// ==========================================
@Composable
fun InlineKnob(
    value: Float,
    onChange: (Float) -> Unit,
    label: String,
    min: Float,
    max: Float,
    accent: Color
) {
    val normalized = ((value - min) / (max - min)).coerceIn(0f, 1f)
    val animRot by animateFloatAsState(targetValue = normalized, label = "rot")

    Box(modifier = Modifier.size(100.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val sens = 0.005f
                        var newVal = value + (-dragAmount.y * sens * (max - min))
                        newVal = newVal.coerceIn(min, max)
                        onChange(newVal)
                    }
                }
        ) {
            val cX = size.width / 2f
            val cY = size.height / 2f
            val r = (size.minDimension / 2f) * 0.85f
            
            // Ticks
            for (i in 0 until 20) {
                val ang = (i.toFloat() / 20) * 2 * PI - PI / 2
                val x1 = cX + cos(ang).toFloat() * (r * 0.9f)
                val y1 = cY + sin(ang).toFloat() * (r * 0.9f)
                val x2 = cX + cos(ang).toFloat() * r
                val y2 = cY + sin(ang).toFloat() * r
                drawLine(Color.DarkGray.copy(alpha = 0.5f), Offset(x1,y1), Offset(x2,y2), strokeWidth = 2f)
            }
            
            // Body Gradient
            val brush = Brush.radialGradient(
                colors = listOf(Color(0xFFEEEEEE), Color(0xFF999999), Color(0xFF333333)),
                center = Offset(cX - r*0.3f, cY - r*0.3f),
                radius = r * 1.5f
            )
            drawCircle(brush = brush, radius = r * 0.7f, center = Offset(cX, cY))
            drawCircle(Color.Black.copy(alpha = 0.3f), radius = r * 0.7f, center = Offset(cX, cY), style = Stroke(width = 4f))
            
            // Pointer
            val deg = -135f + (animRot * 270f)
            val rad = Math.toRadians(deg.toDouble()).toFloat()
            val ex = cX + cos(rad) * (r * 0.6f)
            val ey = cY + sin(rad) * (r * 0.6f)
            drawLine(accent, Offset(cX,cY), Offset(ex,ey), strokeWidth = 6f, cap = StrokeCap.Round)
            drawCircle(accent, radius = 4f, center = Offset(ex,ey))
            
            // Cap
            drawCircle(Color(0xFF111111), radius = r * 0.2f, center = Offset(cX,cY))
        }
        
        Text(label, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

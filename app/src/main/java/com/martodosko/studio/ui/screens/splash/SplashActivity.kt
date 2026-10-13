// ==================================================
// FILE: SplashActivity.kt
// VERSION: v2.3.0-phase1-step2
// PURPOSE: Dedicated Entry Point with Minimal Logic
// NOTE: No ViewModels, No Network Calls. Pure Compose Animation.
// ==================================================
package com.martodosko.studio.ui.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martodosko.studio.ui.screens.MainActivity
import com.martodosko.studio.ui.theme.MartodoskoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MartodoskoTheme {
                SplashScreenContent(
                    onNavigateToMain = {
                        // Navigate to Main Activity after delay
                        startActivity(Intent(this, MainActivity::class.java))
                        finish() // Close splash so back button doesn't return here
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreenContent(onNavigateToMain: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500L) // Simulate loading time (can be adjusted later)
        onNavigateToMain()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Deep Black Background
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            
            Text(
                text = "MARTODOSKO",
                color = Color(0xFF40E0D0), // Neon Cyan
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "AUDIO STUDIO",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(
                color = Color(0xFF40E0D0),
                strokeWidth = 4.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Initializing Engine...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

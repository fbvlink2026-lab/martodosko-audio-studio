// ==================================================
// FILE: SplashActivity.kt
// VERSION: 2.1.0 - CRASH-FREE STATIC BOOT
// PURPOSE: Minimalist Entry Point to Bypass DI Issues
// NOTE: Removed SplashViewModel dependency for stability
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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MartodoskoTheme {
                // ✅ NO VIEWMODEL HERE. Pure Compose State.
                SplashScreenContent(
                    onNavigateToHome = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreenContent(onNavigateToHome: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }

    // Simulate a short loading delay (e.g., 1.5 seconds)
    LaunchedEffect(Unit) {
        delay(1500L) 
        isLoading = false
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF081218)), // Deep Black BG
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            
            // Logo / Title
            Text(
                text = "MARTODOSKO",
                color = Color(0xFF40E0D0), // Neon Cyan
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "AUDIO STUDIO v2.1",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF40E0D0))
                Spacer(modifier = Modifier.height(10.dp))
                Text("Initializing...", color = Color.White, fontSize = 12.sp)
            } else {
                Text("✅ Ready", color = Color(0xFF4CAF50), fontSize = 14.sp)
            }
        }
    }
}

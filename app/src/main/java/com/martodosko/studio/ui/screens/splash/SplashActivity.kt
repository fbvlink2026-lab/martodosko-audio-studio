// ==================================================
// FILE: SplashActivity.kt
// VERSION: 2.1.0 - DEBUG TRACE ENABLED
// PURPOSE: Entry Point with Step-by-Step Verification
// NOTE: Uses Toasts to trace execution flow in real-time
// ==================================================
package com.martodosko.studio.ui.screens.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
        
        // ✅ LOG POINT 1: Activity Created
        Log.d("SPLASH_DEBUG", "1. onCreate called")
        showToastSafe("1. Splash Loaded")

        setContent {
            // ✅ LOG POINT 2: Compose Setup Started
            Log.d("SPLASH_DEBUG", "2. setContent started")
            
            MartodoskoTheme {
                SplashScreenContent(
                    onNavigateToHome = {
                        // ✅ LOG POINT 3: Navigation Triggered
                        Log.d("SPLASH_DEBUG", "3. Navigating to Main...")
                        showToastSafe("3. Going to Home...")
                        
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    private fun showToastSafe(msg: String) {
        try {
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("SPLASH_DEBUG", "Toast failed: ${e.message}")
        }
    }
}

@Composable
fun SplashScreenContent(onNavigateToHome: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // ✅ LOG POINT 4: Coroutine Started
        Log.d("SPLASH_DEBUG", "4. LaunchedEffect started")
        
        delay(1500L) 
        
        // ✅ LOG POINT 5: Delay Finished
        Log.d("SPLASH_DEBUG", "5. Delay finished, triggering nav")
        isLoading = false
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF081218)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            
            Text(
                text = "MARTODOSKO",
                color = Color(0xFF40E0D0),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "AUDIO STUDIO v2.1 (DEBUG)",
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

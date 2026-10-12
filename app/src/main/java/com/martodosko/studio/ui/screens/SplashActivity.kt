// ==================================================
// FILE: ui/screens/splash/SplashActivity.kt
// VERSION: 2.1.0
// PURPOSE: Entry Point with Dynamic Asset Loading
// ==================================================
package com.martodosko.studio.ui.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.martodosko.studio.R
import com.martodosko.studio.ui.screens.MainActivity
import com.martodosko.studio.ui.theme.MartodoskoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MartodoskoTheme {
                SplashScreen(
                    onNavigateToHome = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onError = { msg ->
                        // Show AlertDialog or Snackbar for error
                        println("Error: $msg")
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onError: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is SplashUiState.Ready -> onNavigateToHome()
            is SplashUiState.Error -> onError((uiState as SplashUiState.Error).message)
            else -> {}
        }
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

            // Status Indicator
            when (val state = uiState) {
                is SplashUiState.Loading -> {
                    CircularProgressIndicator(color = Color(0xFF40E0D0))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Initializing...", color = Color.White, fontSize = 12.sp)
                }
                is SplashUiState.Updating -> {
                    LinearProgressIndicator(
                        progress = { state.progress },
                        color = Color(0xFF40E0D0),
                        trackColor = Color.DarkGray,
                        modifier = Modifier.width(200.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(state.message, color = Color.White, fontSize = 12.sp)
                }
                is SplashUiState.Ready -> {
                    Text("✅ System Ready", color = Color(0xFF4CAF50), fontSize = 14.sp)
                }
                is SplashUiState.Error -> {
                    Text("⚠️ ${state.message}", color = Color(0xFFFF5252), fontSize = 12.sp)
                    Button(onClick = { /* Retry Logic Here */ }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

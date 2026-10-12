// ==================================================
// FILE: MainActivity.kt
// VERSION: 2.1.0
// PURPOSE: Main Host Activity for Compose UI
// ==================================================
package com.martodosko.studio.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martodosko.studio.ui.components.ProAudioKnob
import com.martodosko.studio.ui.theme.MartodoskoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MartodoskoTheme {
                // Simple Box to center content
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        
                        Text(
                            text = "MARTODOSKO AUDIO STUDIO",
                            color = Color(0xFF40E0D0), // Neon Cyan
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(30.dp))
                        
                        // ✅ THE TEST COMPONENT
                        var knobValue by remember { mutableStateOf(50f) }
                        
                        ProAudioKnob(
                            value = knobValue,
                            onValueChange = { newValue ->
                                knobValue = newValue
                            },
                            label = "TEST KNOB",
                            minValue = 0f,
                            maxValue = 100f,
                            size = 120.dp // Large enough to see details on phone
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = "Value: ${knobValue.toInt()}",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ==================================================
// FILE: MainActivity.kt
// VERSION: 2.1.0 - PHASE 2 STEP 9
// PURPOSE: Main Host Activity for Compose UI (Pedal Board)
// CHANGE LOG: Replaced single knob test with full PedalBoardScreen
// ==================================================
package com.martodosko.studio.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.martodosko.studio.ui.screens.home.PedalBoardScreen // ✅ IMPORT NEW SCREEN
import com.martodosko.studio.ui.theme.MartodoskoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MartodoskoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // ✅ RENDER THE FULL PEDAL BOARD HERE
                    PedalBoardScreen()
                }
            }
        }
    }
}

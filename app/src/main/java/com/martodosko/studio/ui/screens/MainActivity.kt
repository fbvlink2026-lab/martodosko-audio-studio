// ==================================================
// FILE: MainActivity.kt
// VERSION: v2.3.0-phase1-step4-clean
// PURPOSE: Pure Host Activity for Hilt & Lifecycle
// NOTE: Delegates all UI/Navigation to MainNavGraph.
// ==================================================
package com.martodosko.studio.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.martodosko.studio.ui.navigation.MainNavGraph
import com.martodosko.studio.ui.theme.MartodoskoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("MAIN_ACT", "✅ Starting Main Activity (Delegating to NavGraph)")

        setContent {
            MartodoskoTheme {
                // The entire app shell is now handled by this single composable
                MainNavGraph(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

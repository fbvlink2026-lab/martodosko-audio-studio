// ==================================================
// FILE: PedalBoardScreen.kt
// VERSION: v2.3.0-phase1-step3
// PURPOSE: Dedicated Home Screen with Vocal/Guitar Chains
// NOTE: Extracted from MainActivity for modularity. Uses ProAudioKnob.
// ==================================================
package com.martodosko.studio.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martodosko.studio.ui.components.ProAudioKnob

@Composable
fun PedalBoardScreen(modifier: Modifier = Modifier) {
    // Local State for Knobs (Will be replaced by ViewModel in Phase 2)
    var vocalGain by remember { mutableStateOf(0f) }
    var guitarDrive by remember { mutableStateOf(0f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionHeader(title = "🎤 VOCAL CHAIN", color = Color.Cyan)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, 
                modifier = Modifier.fillMaxWidth()
            ) {
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
            SectionHeader(title = "🎸 GUITAR CHAIN", color = Color(0xFFFF9800))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, 
                modifier = Modifier.fillMaxWidth()
            ) {
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
    Text(
        text = title, 
        color = color, 
        fontSize = 18.sp, 
        fontWeight = FontWeight.Bold, 
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

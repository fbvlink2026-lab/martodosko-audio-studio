// ==================================================
// FILE: PedalBoardScreen.kt
// VERSION: v2.3.0-phase2-step2-wired
// PURPOSE: Dedicated Home Screen Connected to State Manager
// NOTE: Uses PedalBoardViewModel for reactive updates.
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.martodosko.studio.ui.components.ProAudioKnob

@Composable
fun PedalBoardScreen(
    modifier: Modifier = Modifier,
    viewModel: PedalBoardViewModel = hiltViewModel()
) {
    // Collect the latest state from the ViewModel
    val params by viewModel.audioParams.collectAsState()

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
                    value = params.vocalGain, // ✅ FROM STATE MANAGER
                    onValueChange = { newValue -> 
                        viewModel.onVocalGainChange(newValue) // ✅ SEND TO VM
                    },
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
                    value = params.guitarDrive, // ✅ FROM STATE MANAGER
                    onValueChange = { newValue -> 
                        viewModel.onGuitarDriveChange(newValue) // ✅ SEND TO VM
                    },
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

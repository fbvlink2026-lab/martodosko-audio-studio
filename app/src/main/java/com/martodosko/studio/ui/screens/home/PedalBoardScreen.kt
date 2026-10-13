// ==================================================
// FILE: PedalBoardScreen.kt
// VERSION: v2.3.0-phase2-pedal-redesign
// PURPOSE: Realistic Stompbox Chain Interface
// NOTE: Each pedal has On/Off, Effect, NG, Vol controls.
// ==================================================
package com.martodosko.studio.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val params by viewModel.audioParams.collectAsState()

    // Horizontal Scroll for the Pedal Chain
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // VOCAL PEDAL CARD
        PedalCard(
            title = "VOCAL CHAIN",
            accentColor = Color.Cyan,
            isActive = true, // Mock state for now
            gain = params.vocalGain,
            volume = params.vocalVolume,
            onGainChange = { viewModel.onVocalGainChange(it) },
            onVolChange = { /* Handle Vocal Vol */ }
        )

        // GUITAR PEDAL CARD
        PedalCard(
            title = "GUITAR DRIVE",
            accentColor = Color(0xFFFF9800),
            isActive = true,
            gain = params.guitarDrive,
            volume = params.guitarLevel,
            onGainChange = { viewModel.onGuitarDriveChange(it) },
            onVolChange = { /* Handle Guitar Level */ }
        )
    }
}

@Composable
fun PedalCard(
    title: String,
    accentColor: Color,
    isActive: Boolean,
    gain: Float,
    volume: Float,
    onGainChange: (Float) -> Unit,
    onVolChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(320.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        border = BorderStroke(2.dp, if(isActive) accentColor else Color.DarkGray)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Title + Power Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                
                IconButton(onClick = { /* Toggle Active State */ }) {
                    Icon(
                        imageVector = Icons.Default.Power,
                        contentDescription = "Power",
                        tint = if (isActive) accentColor else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Top Row: Effect Selector (Mock Dropdown/Button)
            OutlinedButton(
                onClick = { /* Open Effect Menu */ },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("CHORUS", fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Middle: Two Knobs Side-by-Side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProAudioKnob(
                    value = gain,
                    onValueChange = onGainChange,
                    label = "FX AMT",
                    unit = "%",
                    minValue = 0f,
                    maxValue = 100f,
                    knobSize = 60.dp,
                    accentColor = accentColor
                )
                ProAudioKnob(
                    value = 50f, // Mock Noise Gate
                    onValueChange = { },
                    label = "NGATE",
                    unit = "",
                    minValue = 0f,
                    maxValue = 100f,
                    knobSize = 60.dp,
                    accentColor = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom: Volume Knob (Large)
            ProAudioKnob(
                value = volume,
                onValueChange = onVolChange,
                label = "LEVEL",
                unit = "%",
                minValue = 0f,
                maxValue = 100f,
                knobSize = 80.dp,
                accentColor = Color.White
            )
        }
    }
}

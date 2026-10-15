// ==================================================
// FILE: PedalBoardScreen.kt
// VERSION: v2.3.0-global-sync-board
// PURPOSE: Detachable Stompbox Chain Fully Synced with Global State
// NOTE: Replaced local list state with direct binding to AudioParams.
// ==================================================
package com.martodosko.studio.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.martodosko.studio.ui.components.NativeKnob

@Composable
fun PedalBoardScreen(
    modifier: Modifier = Modifier,
    viewModel: PedalBoardViewModel = hiltViewModel()
) {
    // ✅ OBSERVE GLOBAL STATE
    val params by viewModel.audioParams.collectAsState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .horizontalScroll(rememberScrollState())
            .padding(PaddingValues(16.dp)),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // VOCAL PEDAL CARD
        PedalCard(
            title = "VOCAL CHAIN",
            accentColor = Color.Cyan,
            isActive = true,
            gain = params.vocalGain,
            volume = params.vocalVolume,
            gate = 20f, // Mocked for now until we add gate to AudioParams
            onGainChange = { viewModel.onVocalGainChange(it) },
            onVolChange = { viewModel.onVocalVolumeChange(it) },
            onToggleActive = { /* Toggle Logic */ },
            onRemove = { /* Remove Logic */ }
        )

        // GUITAR PEDAL CARD
        PedalCard(
            title = "GUITAR DRIVE",
            accentColor = Color(0xFFFF9800),
            isActive = true,
            gain = params.guitarDrive,
            volume = params.guitarLevel,
            gate = 10f, // Mocked
            onGainChange = { viewModel.onGuitarDriveChange(it) },
            onVolChange = { viewModel.onGuitarLevelChange(it) },
            onToggleActive = { /* Toggle Logic */ },
            onRemove = { /* Remove Logic */ }
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
    gate: Float,
    onGainChange: (Float) -> Unit,
    onVolChange: (Float) -> Unit,
    onToggleActive: () -> Unit,
    onRemove: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title.uppercase(), color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Detach", tint = Color.Red, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = onToggleActive,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if(isActive) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                    contentColor = if(isActive) accentColor else Color.Gray
                ),
                border = BorderStroke(1.dp, if(isActive) accentColor else Color.Gray)
            ) {
                Icon(Icons.Default.Power, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if(isActive) "ON" else "OFF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // FX/GAIN KNOB - SYNCED TO GLOBAL STATE
                NativeKnob(
                    value = gain,
                    onValueChange = onGainChange,
                    label = "FX AMT",
                    unit = "%",
                    minValue = 0f,
                    maxValue = 100f,
                    modifier = Modifier.size(70.dp)
                )
                
                // NOISE GATE KNOB - MOCKED (Not yet in AudioParams)
                NativeKnob(
                    value = gate,
                    onValueChange = { }, 
                    label = "NGATE",
                    unit = "%",
                    minValue = 0f,
                    maxValue = 100f,
                    modifier = Modifier.size(70.dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))

            // VOLUME KNOB - SYNCED TO GLOBAL STATE
            NativeKnob(
                value = volume,
                onValueChange = onVolChange,
                label = "LEVEL",
                unit = "%",
                minValue = 0f,
                maxValue = 100f,
                modifier = Modifier.size(90.dp)
            )
        }
    }
}

// ==================================================
// FILE: PedalBoardScreen.kt
// VERSION: v2.3.0-phase2-detachable-board
// PURPOSE: Detachable Stompbox Chain Interface
// NOTE: Pedals are added from Library. Can be removed.
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.martodosko.studio.ui.components.ProAudioKnob

// Mock Data Structure for Active Pedals
data class ActivePedal(
    val id: String,
    val name: String,
    val type: String, // "VOCAL" or "GUITAR"
    val fxParam: Float,
    val gateParam: Float,
    val volParam: Float,
    val isActive: Boolean
)

@Composable
fun PedalBoardScreen(
    modifier: Modifier = Modifier,
    viewModel: PedalBoardViewModel = hiltViewModel()
) {
    val params by viewModel.audioParams.collectAsState()
    
    // Local list of active pedals (In real app, this comes from Preset State)
    // Initializing with one Vocal and one Guitar pedal for demonstration
    val activePedals = remember { 
        mutableStateListOf(
            ActivePedal("ped-1", "Chorus", "VOCAL", 50f, 20f, 80f, true),
            ActivePedal("ped-2", "Overdrive", "GUITAR", 65f, 10f, 75f, true)
        )
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .horizontalScroll(rememberScrollState())
            .padding(PaddingValues(16.dp)),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (activePedals.isEmpty()) {
             Text("No Pedals Selected.\nGo to Presets Library to add.", color = Color.Gray, modifier = Modifier.padding(top = 100.dp))
        } else {
            activePedals.forEachIndexed { index, pedal ->
                PedalCard(
                    pedal = pedal,
                    onUpdateFx = { newVal -> 
                        // Update local list
                        activePedals[index] = pedal.copy(fxParam = newVal)
                        // Sync to Global State if needed
                        if(pedal.type == "VOCAL") viewModel.onVocalGainChange(newVal)
                        else viewModel.onGuitarDriveChange(newVal)
                    },
                    onUpdateVol = { newVal -> 
                         activePedals[index] = pedal.copy(volParam = newVal)
                    },
                    onRemove = {
                        activePedals.removeAt(index)
                    },
                    onToggleActive = {
                        activePedals[index] = pedal.copy(isActive = !pedal.isActive)
                    }
                )
            }
        }
    }
}

@Composable
fun PedalCard(
    pedal: ActivePedal,
    onUpdateFx: (Float) -> Unit,
    onUpdateVol: (Float) -> Unit,
    onRemove: () -> Unit,
    onToggleActive: () -> Unit
) {
    val accent = if (pedal.type == "VOCAL") Color.Cyan else Color(0xFFFF9800)
    
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(320.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        border = BorderStroke(2.dp, if(pedal.isActive) accent else Color.DarkGray)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Name + Remove Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(pedal.name.uppercase(), color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Detach", tint = Color.Red, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Power Switch Visual
            OutlinedButton(
                onClick = onToggleActive,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if(pedal.isActive) accent.copy(alpha = 0.2f) else Color.Transparent,
                    contentColor = if(pedal.isActive) accent else Color.Gray
                ),
                border = BorderStroke(1.dp, if(pedal.isActive) accent else Color.Gray)
            ) {
                Icon(Icons.Default.Power, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if(pedal.isActive) "ON" else "OFF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Controls Grid (2x2 roughly)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProAudioKnob(
                    value = pedal.fxParam,
                    onValueChange = onUpdateFx,
                    label = "FX",
                    unit = "%",
                    minValue = 0f,
                    maxValue = 100f,
                    knobSize = 60.dp,
                    accentColor = accent
                )
                ProAudioKnob(
                    value = pedal.gateParam,
                    onValueChange = { /* Gate Logic */ },
                    label = "NG",
                    unit = "%",
                    minValue = 0f,
                    maxValue = 100f,
                    knobSize = 60.dp,
                    accentColor = Color.LightGray
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))

            // Volume Knob (Large, Bottom)
            ProAudioKnob(
                value = pedal.volParam,
                onValueChange = onUpdateVol,
                label = "VOL",
                unit = "%",
                minValue = 0f,
                maxValue = 100f,
                knobSize = 80.dp,
                accentColor = Color.White
            )
        }
    }
}

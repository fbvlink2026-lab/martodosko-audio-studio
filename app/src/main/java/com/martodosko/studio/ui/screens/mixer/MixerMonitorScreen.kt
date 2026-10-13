// ==================================================
// FILE: MixerMonitorScreen.kt
// VERSION: v2.3.0-phase2-step3-knobs-only
// PURPOSE: Detailed Channel View with ROTARY KNOBS
// NOTE: Consistent with Strategic Master Plan v2.3 Visual Style. No Sliders.
// ==================================================
package com.martodosko.studio.ui.screens.mixer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.martodosko.studio.ui.components.ProAudioKnob

@Composable
fun MixerMonitorScreen(
    modifier: Modifier = Modifier,
    viewModel: MixerViewModel = hiltViewModel()
) {
    val params by viewModel.audioParams.collectAsState()

    // Simple Tab Row for Vocal vs Guitar focus
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("VOCAL CH", "GUITAR CH")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        
        // Header
        Text(
            text = "MIXER CONSOLE",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { 
                        Text(
                            title, 
                            color = if (selectedTab == index) Color.Cyan else Color.Gray,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        ) 
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content Area based on Tab
        when (selectedTab) {
            0 -> VocalChannelPanel(params.vocalGain, params.vocalVolume, viewModel)
            1 -> GuitarChannelPanel(params.guitarDrive, params.guitarLevel, viewModel)
        }
        
        // Master Section (Always visible at bottom)
        Spacer(modifier = Modifier.weight(1f))
        MasterPanel(params.masterVolume, viewModel)
    }
}

// --- VOCAL CHANNEL PANEL ---
@Composable
fun VocalChannelPanel(gain: Float, volume: Float, vm: MixerViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12232E)) // Panel Gray
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            
            Text("INPUT GAIN", color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            ProAudioKnob(
                value = gain,
                onValueChange = { /* Read-only display or sync back to Home logic */ 
                    // Note: In a real app, changing this here might update the global state too
                    vm.updateParam { it.copy(vocalGain = it) } 
                },
                label = "${gain.toInt()} dB",
                minValue = -50f,
                maxValue = 50f,
                knobSize = 90.dp,
                accentColor = Color.Cyan
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("CHANNEL VOLUME", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            ProAudioKnob(
                value = volume,
                onValueChange = { newValue -> vm.onVocalVolumeChange(newValue) },
                label = "${volume.toInt()}%",
                minValue = 0f,
                maxValue = 100f,
                knobSize = 90.dp,
                accentColor = Color.White
            )
        }
    }
}

// --- GUITAR CHANNEL PANEL ---
@Composable
fun GuitarChannelPanel(drive: Float, level: Float, vm: MixerViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12232E)) // Panel Gray
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            
            Text("DRIVE AMOUNT", color = Color(0xFFFF9800), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            ProAudioKnob(
                value = drive,
                onValueChange = { /* Sync logic */ 
                    vm.updateParam { it.copy(guitarDrive = it) }
                },
                label = "${drive.toInt()}%",
                minValue = -50f,
                maxValue = 50f,
                knobSize = 90.dp,
                accentColor = Color(0xFFFF9800)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("OUTPUT LEVEL", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            ProAudioKnob(
                value = level,
                onValueChange = { newValue -> vm.onGuitarLevelChange(newValue) },
                label = "${level.toInt()}%",
                minValue = 0f,
                maxValue = 100f,
                knobSize = 90.dp,
                accentColor = Color.White
            )
        }
    }
}

// --- MASTER OUTPUT PANEL ---
@Composable
fun MasterPanel(volume: Float, vm: MixerViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)) // Darker Gray for distinction
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("MASTER OUT", color = Color.Green, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Global Output Control", color = Color.Gray, fontSize = 10.sp)
            }
            
            ProAudioKnob(
                value = volume,
                onValueChange = { newValue -> vm.onMasterVolumeChange(newValue) },
                label = "${volume.toInt()}%",
                minValue = 0f,
                maxValue = 100f,
                knobSize = 70.dp, // Slightly smaller for master
                accentColor = Color.Green
            )
        }
    }
}

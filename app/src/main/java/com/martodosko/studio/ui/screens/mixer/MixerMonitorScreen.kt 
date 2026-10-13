// ==================================================
// FILE: MixerMonitorScreen.kt
// VERSION: v2.3.0-phase2-step3
// PURPOSE: Detailed Channel View with Sliders
// NOTE: Demonstrates Bidirectional Sync via shared StateFlow.
// ==================================================
package com.martodosko.studio.ui.screens.mixer

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
import com.martodosko.studio.ui.theme.MartodoskoTheme

@Composable
fun MixerMonitorScreen(
    modifier: Modifier = Modifier,
    viewModel: MixerViewModel = hiltViewModel()
) {
    val params by viewModel.audioParams.collectAsState()

    // Simple Tab Row for Vocal vs Guitar focus (Visual only for now)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("VOCAL", "GUITAR")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        
        // Header
        Text(
            text = "MIXER CHANNELS",
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
                    text = { Text(title, color = if (selectedTab == index) Color.Cyan else Color.Gray) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content Area based on Tab
        when (selectedTab) {
            0 -> VocalChannelControls(params.vocalGain, params.vocalVolume, viewModel)
            1 -> GuitarChannelControls(params.guitarDrive, params.guitarLevel, viewModel)
        }
        
        // Master Section (Always visible at bottom)
        Spacer(modifier = Modifier.weight(1f))
        MasterSection(params.masterVolume, viewModel)
    }
}

@Composable
fun VocalChannelControls(gain: Float, volume: Float, vm: MixerViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SliderControl(label = "INPUT GAIN", value = gain, min = -50f, max = 50f, color = Color.Cyan, onValueChange = { /* Gain is controlled by Knob in Home, but we can display it here */ })
        SliderControl(label = "CHANNEL VOL", value = volume, min = 0f, max = 100f, color = Color.White, onValueChange = { vm.onVocalVolumeChange(it) })
    }
}

@Composable
fun GuitarChannelControls(drive: Float, level: Float, vm: MixerViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SliderControl(label = "DRIVE AMT", value = drive, min = -50f, max = 50f, color = Color(0xFFFF9800), onValueChange = { /* Drive is controlled by Knob in Home */ })
        SliderControl(label = "OUTPUT LVL", value = level, min = 0f, max = 100f, color = Color.White, onValueChange = { vm.onGuitarLevelChange(it) })
    }
}

@Composable
fun MasterSection(volume: Float, vm: MixerViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("MASTER OUTPUT", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            SliderControl(label = "MAIN VOL", value = volume, min = 0f, max = 100f, color = Color.Green, onValueChange = { vm.onMasterVolumeChange(it) })
        }
    }
}

// Reusable Slider Component
@Composable
fun SliderControl(label: String, value: Float, min: Float, max: Float, color: Color, onValueChange: (Float) -> Unit) {
    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(label, color = Color.LightGray, fontSize = 12.sp)
            Text(String.format("%.1f", value), color = color, fontSize = 12.sp)
        }
        Slider(
            value = value.coerceIn(min, max),
            onValueChange = onValueChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
        )
    }
}

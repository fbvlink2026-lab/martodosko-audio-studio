// ==================================================
// FILE: MixerMonitorScreen.kt
// VERSION: v2.3.0-phase2-mixer-responsive
// PURPOSE: Channel Strips with Horizontal Scrolling
// NOTE: Optimized for Portrait (Tabs) and Landscape (Side-by-Side).
// ==================================================
package com.martodosko.studio.ui.screens.mixer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenHeightDp < configuration.screenWidthDp

    if (isLandscape) {
        // LANDSCAPE MODE: Side-by-side Channels
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            ChannelStrip(
                title = "VOCAL",
                gain = params.vocalGain,
                vol = params.vocalVolume,
                color = Color.Cyan,
                onVolChange = { viewModel.onVocalVolumeChange(it) }
            )
            ChannelStrip(
                title = "GUITAR",
                gain = params.guitarDrive,
                vol = params.guitarLevel,
                color = Color(0xFFFF9800),
                onVolChange = { viewModel.onGuitarLevelChange(it) }
            )
            ChannelStrip(
                title = "MASTER",
                gain = 0f,
                vol = params.masterVolume,
                color = Color.Green,
                onVolChange = { viewModel.onMasterVolumeChange(it) }
            )
        }
    } else {
        // PORTRAIT MODE: Tabs
        var selectedTab by remember { mutableIntStateOf(0) }
        val tabs = listOf("VOCAL", "GUITAR", "MASTER")

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
             TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, color = if (selectedTab == index) Color.Cyan else Color.Gray) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            when(selectedTab) {
                0 -> ChannelStripFull(params.vocalGain, params.vocalVolume, Color.Cyan) { viewModel.onVocalVolumeChange(it) }
                1 -> ChannelStripFull(params.guitarDrive, params.guitarLevel, Color(0xFFFF9800)) { viewModel.onGuitarLevelChange(it) }
                2 -> ChannelStripFull(0f, params.masterVolume, Color.Green) { viewModel.onMasterVolumeChange(it) }
            }
        }
    }
}

// Compact Strip for Landscape
@Composable
fun ChannelStrip(title: String, gain: Float, vol: Float, color: Color, onVolChange: (Float)->Unit) {
    Card(
        modifier = Modifier.width(140.dp).height(300.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12232E))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = color, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            ProAudioKnob(
                value = gain,
                onValueChange = {}, // Read only in mixer view for now
                label = "IN",
                knobSize = 50.dp,
                accentColor = color,
                isEnabled = false
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ProAudioKnob(
                value = vol,
                onValueChange = onVolChange,
                label = "OUT",
                knobSize = 70.dp,
                accentColor = Color.White
            )
        }
    }
}

// Full View for Portrait
@Composable
fun ChannelStripFull(gain: Float, vol: Float, color: Color, onVolChange: (Float)->Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("INPUT GAIN", color = color, fontSize = 12.sp)
        ProAudioKnob(
            value = gain,
            onValueChange = {},
            label = "READ ONLY",
            knobSize = 100.dp,
            accentColor = color,
            isEnabled = false
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("CHANNEL VOLUME", color = Color.White, fontSize = 12.sp)
        ProAudioKnob(
            value = vol,
            onValueChange = onVolChange,
            label = "EDITABLE",
            knobSize = 120.dp,
            accentColor = Color.White
        )
    }
}

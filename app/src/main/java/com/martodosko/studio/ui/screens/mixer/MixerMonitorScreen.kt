// ==================================================
// FILE: MixerMonitorScreen.kt
// VERSION: v2.3.0-phase2-mixer-spacious
// PURPOSE: Dual Channel Mixer with Proper Spacing for Large Knobs
// NOTE: Adjusted card widths and heights to accommodate new knob aesthetics.
// ==================================================
package com.martodosko.studio.ui.screens.mixer

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
import androidx.compose.foundation.layout.width
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
        // LANDSCAPE: Side-by-side Channels
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .horizontalScroll(rememberScrollState())
                .padding(PaddingValues(16.dp)),
            horizontalArrangement = Arrangement.spacedBy(24.dp) // More space between strips
        ) {
            ChannelStrip(
                title = "VOCAL CH",
                gain = params.vocalGain,
                vol = params.vocalVolume,
                color = Color.Cyan,
                onVolChange = { viewModel.onVocalVolumeChange(it) }
            )
            ChannelStrip(
                title = "GUITAR CH",
                gain = params.guitarDrive,
                vol = params.guitarLevel,
                color = Color(0xFFFF9800),
                onVolChange = { viewModel.onGuitarLevelChange(it) }
            )
        }
    } else {
        // PORTRAIT: Tabs
        var selectedTab by remember { mutableIntStateOf(0) }
        val tabs = listOf("VOCAL", "GUITAR")

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
            }
        }
    }
}

// Compact Strip for Landscape - Made Wider/Taller
@Composable
fun ChannelStrip(title: String, gain: Float, vol: Float, color: Color, onVolChange: (Float)->Unit) {
    Card(
        modifier = Modifier.width(160.dp).height(380.dp), // Increased dimensions
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12232E))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(20.dp))
            
            ProAudioKnob(
                value = gain,
                onValueChange = {},
                label = "IN GAIN",
                knobSize = 90.dp, // Standardized size
                accentColor = color,
                isEnabled = false,
                unit = "dB"
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            ProAudioKnob(
                value = vol,
                onValueChange = onVolChange,
                label = "OUT VOL",
                knobSize = 110.dp, // Primary control larger
                accentColor = Color.White,
                unit = "%"
            )
        }
    }
}

// Full View for Portrait - Centered and Spacious
@Composable
fun ChannelStripFull(gain: Float, vol: Float, color: Color, onVolChange: (Float)->Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("INPUT GAIN", color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ProAudioKnob(
            value = gain,
            onValueChange = {},
            label = "SYNCED FROM BOARD",
            knobSize = 140.dp, // Big display knob
            accentColor = color,
            isEnabled = false,
            unit = "dB"
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text("CHANNEL VOLUME", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ProAudioKnob(
            value = vol,
            onValueChange = onVolChange,
            label = "EDITABLE",
            knobSize = 160.dp, // Main interactive knob
            accentColor = Color.White,
            unit = "%"
        )
    }
}

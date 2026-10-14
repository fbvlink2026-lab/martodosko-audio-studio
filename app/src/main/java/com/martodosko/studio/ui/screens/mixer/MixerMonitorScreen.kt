// ==================================================
// FILE: MixerMonitorScreen.kt
// VERSION: v2.3.0-phase2-mixer-label-fix-final
// PURPOSE: Dual Channel Mixer with Internal Labels
// NOTE: Removed external text headers. Labels are now drawn inside KnobView canvas.
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
import androidx.compose.foundation.layout.size
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
import com.martodosko.studio.ui.components.NativeKnob // ✅ USE NATIVE KNOB

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
            horizontalArrangement = Arrangement.spacedBy(24.dp)
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
        modifier = Modifier.width(160.dp).height(380.dp), 
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12232E))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // External Title kept for identification in landscape list view
            Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            // Knob with INTERNAL Label "IN"
            NativeKnob(
                value = gain,
                onValueChange = {}, // Read only display usually
                label = "IN",       // ✅ LABEL INSIDE CANVAS
                unit = "dB",
                minValue = -50f,
                maxValue = 50f,
                modifier = Modifier.size(90.dp)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Knob with INTERNAL Label "OUT"
            NativeKnob(
                value = vol,
                onValueChange = onVolChange,
                label = "OUT",      // ✅ LABEL INSIDE CANVAS
                unit = "%",
                minValue = 0f,
                maxValue = 100f,
                modifier = Modifier.size(110.dp)
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
        // ✅ NO EXTERNAL TEXT HEADER HERE ANYMORE
        
        // First Knob: Gain/Input
        // The label "GAIN" will be drawn by the KnobView itself at the top of its panel
        NativeKnob(
            value = gain,
            onValueChange = {}, // Read-only in mixer view for now to avoid sync conflicts
            label = "GAIN",     // ✅ THIS REPLACES "SYNCED FROM BOARD" POSITION
            unit = "dB",
            minValue = -50f,
            maxValue = 50f,
            modifier = Modifier.size(160.dp) // Large enough to show internal label clearly
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Second Knob: Volume/Output
        // The label "VOL" will be drawn by the KnobView itself
        NativeKnob(
            value = vol,
            onValueChange = onVolChange,
            label = "VOL",      // ✅ THIS REPLACES "EDITABLE" POSITION
            unit = "%",
            minValue = 0f,
            maxValue = 100f,
            modifier = Modifier.size(180.dp) // Largest knob for primary control
        )
    }
}

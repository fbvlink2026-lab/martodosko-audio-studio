package com.martodosko.studio.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.martodosko.studio.ui.theme.NeonCyan
import com.martodosko.studio.ui.theme.PanelGray

@Composable
fun PedalBoardScreen(
    modifier: Modifier = Modifier,
    viewModel: PedalBoardViewModel = hiltViewModel()
) {
    // Observe States
    val vocal by viewModel.vocalState.collectAsState(initial = null)
    val guitar by viewModel.guitarState.collectAsState(initial = null)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black), // Deep Black BG
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        
        // ==========================================
        // SECTION 1: VOCAL CHANNEL
        // ==========================================
        item {
            SectionHeader(title = "🎤 VOCAL CHANNEL", color = NeonCyan)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gain Knob
                ProAudioKnob(
                    value = vocal?.gain ?: 0f,
                    onValueChange = { viewModel.onVocalGainChange(it) },
                    label = "GAIN",
                    minValue = -50f,
                    maxValue = 50f,
                    size = 100.dp,
                    accentColor = NeonCyan
                )
                
                // Volume Knob
                ProAudioKnob(
                    value = vocal?.volume ?: 80f,
                    onValueChange = { viewModel.onVocalVolumeChange(it) },
                    label = "VOL",
                    minValue = 0f,
                    maxValue = 100f,
                    size = 100.dp,
                    accentColor = Color.White
                )
            }
        }

        // Divider
        item { HorizontalDivider(color = PanelGray, thickness = 2.dp) }

        // ==========================================
        // SECTION 2: GUITAR CHANNEL
        // ==========================================
        item {
            SectionHeader(title = "🎸 GUITAR EFFECTS", color = Color(0xFFFF9800)) // Orange Accent
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gain Knob
                ProAudioKnob(
                    value = guitar?.gain ?: 0f,
                    onValueChange = { viewModel.onGuitarGainChange(it) },
                    label = "DRIVE",
                    minValue = -50f,
                    maxValue = 50f,
                    size = 100.dp,
                    accentColor = Color(0xFFFF9800)
                )
                
                // Volume Knob
                ProAudioKnob(
                    value = guitar?.volume ?: 75f,
                    onValueChange = { viewModel.onGuitarVolumeChange(it) },
                    label = "LEVEL",
                    minValue = 0f,
                    maxValue = 100f,
                    size = 100.dp,
                    accentColor = Color.White
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

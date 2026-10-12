package com.martodosko.studio.ui.screens.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Deep Black BG
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color(0xFF40E0D0), // Neon Cyan
            fontSize = 24.sp
        )
    }
}

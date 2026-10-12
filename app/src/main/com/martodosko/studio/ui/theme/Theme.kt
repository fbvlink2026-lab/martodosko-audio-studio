package com.martodosko.studio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = NeonCyan,
    secondary = PurpleAccent,
    tertiary = SuccessGreen,
    background = DeepBlack,
    surface = PanelGray,
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onBackground = MetalLight,
    onSurface = MetalLight
)

@Composable
fun MartodoskoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Force Dark Mode as per Master Plan v2.1
    val colorScheme = DarkColorPalette
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

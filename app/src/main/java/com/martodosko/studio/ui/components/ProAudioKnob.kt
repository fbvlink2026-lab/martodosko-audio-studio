// ==================================================
// FILE: ProAudioKnob.kt
// VERSION: v2.3.0-phase2-knob-upgrade
// PURPOSE: High-Fidelity Rotary Control with Numeric Feedback
// NOTE: Improved drag sensitivity and added value display.
// ==================================================
package com.martodosko.studio.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProAudioKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    knobSize: Dp = 80.dp,
    minValue: Float = 0f,
    maxValue: Float = 100f,
    label: String = "",
    unit: String = "%", // e.g., "%", "dB", ""
    accentColor: Color = Color(0xFF40E0D0),
    isEnabled: Boolean = true,
    showNumericValue: Boolean = true
) {
    val normalizedValue = ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    val animatedRotation by animateFloatAsState(targetValue = normalizedValue, label = "rotation")
    
    // Track if user is currently dragging for visual feedback
    var isDragging by remember { mutableStateOf(false) }

    Box(modifier = modifier.size(knobSize)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!isEnabled) return@pointerInput
                    
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, dragAmount ->
                        change.consume()
                        
                        // Calculate new value based on vertical drag distance
                        // Negative Y means moving up (increase value)
                        val delta = -dragAmount.y 
                        val range = abs(maxValue - minValue)
                        val step = range * 0.005f // Fine-tune sensitivity here
                        
                        var newValue = value + (delta * step)
                        newValue = newValue.coerceIn(minValue, maxValue)
                        onValueChange(newValue)
                    }
                }
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val radius = (size.minDimension / 2f) * 0.85f 
            
            // 1. Draw Outer Ring (Ticks)
            drawOuterRing(Offset(centerX, centerY), radius)
            
            // 2. Draw Knob Body (Metallic Gradient)
            drawKnobBody(Offset(centerX, centerY), radius * 0.7f, isDragging, accentColor)
            
            // 3. Draw Pointer Line
            drawPointer(Offset(centerX, centerY), radius * 0.6f, animatedRotation, accentColor)
            
            // 4. Draw Center Cap
            drawCenterCap(Offset(centerX, centerY), radius * 0.2f)
        }
        
        // 5. Labels & Values
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            if (showNumericValue && !isDragging) {
                 // Show current value when not dragging to avoid jitter
                 Text(
                     text = formatValue(value, unit),
                     color = Color.White.copy(alpha = 0.9f),
                     fontSize = 10.sp,
                     fontWeight = FontWeight.Bold
                 )
            } else if (isDragging) {
                 // Show live updating value while dragging
                 Text(
                     text = formatValue(value, unit),
                     color = accentColor,
                     fontSize = 10.sp,
                     fontWeight = FontWeight.Bold
                 )
            }
            
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    color = Color.Gray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun formatValue(v: Float, unit: String): String {
    return if (v == v.toInt().toFloat()) "${v.toInt()}$unit" else "%.1f%s".format(v, unit)
}

// --- PRIVATE HELPER DRAW FUNCTIONS ---

private fun DrawScope.drawOuterRing(center: Offset, radius: Float) {
    val numTicks = 20
    for (i in 0 until numTicks) {
        val angle = (i.toFloat() / numTicks) * 2 * PI - PI / 2
        val startR = radius * 0.9f
        val endR = radius
        
        val x1 = center.x + cos(angle).toFloat() * startR
        val y1 = center.y + sin(angle).toFloat() * startR
        val x2 = center.x + cos(angle).toFloat() * endR
        val y2 = center.y + sin(angle).toFloat() * endR
        
        drawLine(
            color = Color.DarkGray.copy(alpha = 0.5f),
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawKnobBody(center: Offset, radius: Float, isActive: Boolean, highlightColor: Color) {
    val lightSourceOffset = Offset(-radius * 0.3f, -radius * 0.3f)
    
    // Base Metallic Gradient
    val baseColors = listOf(
        Color(0xFFEEEEEE), 
        Color(0xFFAAAAAA), 
        Color(0xFF444444)  
    )
    
    // If active, add a subtle glow tint
    val finalColors = if (isActive) {
        baseColors.map { it.blend(highlightColor, 0.2f) }
    } else {
        baseColors
    }

    val brush = Brush.radialGradient(
        colors = finalColors,
        center = center + lightSourceOffset,
        radius = radius * 1.5f
    )
    
    drawCircle(brush = brush, radius = radius, center = center)
    
    // Inner Bevel
    drawCircle(
        color = Color.Black.copy(alpha = 0.4f),
        radius = radius,
        center = center,
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawPointer(center: Offset, length: Float, rotationNorm: Float, color: Color) {
    val startAngleDeg = -135f
    val sweepAngleDeg = 270f
    val currentAngleDeg = startAngleDeg + (rotationNorm * sweepAngleDeg)
    val radian = Math.toRadians(currentAngleDeg.toDouble()).toFloat()
    
    val endX = center.x + cos(radian) * length
    val endY = center.y + sin(radian) * length
    
    drawLine(
        color = color,
        start = center,
        end = Offset(endX, endY),
        strokeWidth = 5f,
        cap = StrokeCap.Round
    )
    
    drawCircle(
        color = color,
        radius = 3f,
        center = Offset(endX, endY)
    )
}

private fun DrawScope.drawCenterCap(center: Offset, radius: Float) {
    drawCircle(color = Color(0xFF111111), radius = radius, center = center)
    drawCircle(
        color = Color.White.copy(alpha = 0.15f),
        radius = radius * 0.4f,
        center = Offset(center.x - radius*0.1f, center.y - radius*0.1f)
    )
}

// Helper for blending colors
fun Color.blend(other: Color, fraction: Float): Color {
    val r = red * (1 - fraction) + other.red * fraction
    val g = green * (1 - fraction) + other.green * fraction
    val b = blue * (1 - fraction) + other.blue * fraction
    return Color(red = r, green = g, blue = b, alpha = alpha)
}

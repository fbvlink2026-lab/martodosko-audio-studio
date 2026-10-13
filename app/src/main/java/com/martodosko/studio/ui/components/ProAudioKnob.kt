// ==================================================
// FILE: ProAudioKnob.kt
// VERSION: v2.3.0-phase2-knob-direct-control
// PURPOSE: High-Fidelity Rotary Control with Direct Input Response
// NOTE: Removed animation to fix jitter/back-tracking. Optimized hit area.
// ==================================================
package com.martodosko.studio.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun ProAudioKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    knobSize: Dp = 120.dp, // Increased default size for better visibility
    minValue: Float = 0f,
    maxValue: Float = 100f,
    label: String = "",
    unit: String = "%",
    accentColor: Color = Color(0xFF40E0D0),
    isEnabled: Boolean = true
) {
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
                        
                        // Direct mapping: Vertical drag distance maps to value range
                        // Sensitivity factor: 0.008f allows fine control without being too slow
                        val delta = -dragAmount.y 
                        val range = abs(maxValue - minValue)
                        val step = range * 0.008f 
                        
                        var newValue = value + (delta * step)
                        newValue = newValue.coerceIn(minValue, maxValue)
                        onValueChange(newValue)
                    }
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val halfSize = minOf(size.width, size.height) * 0.5f
            
            // Colors matching Legacy
            val panelColor = Color(0xFF12232E)
            val knobShineStart = Color(0xFF2A5F7A)
            val knobShineEnd = Color(0xFF081218)
            val tickActiveColor = Color(0xFF8AF0E0)
            val tickNormalColor = Color(0xFF3A4A54)
            
            // Dimensions scaled relative to canvas size
            val knobRadius = halfSize * 0.50f // Slightly smaller body to leave room for ticks
            val arcRadius = halfSize * 0.65f
            val tickInner = arcRadius * 1.05f
            val tickOuter = arcRadius * 1.15f

            // 1. DRAW RECTANGULAR PANEL BACKGROUND
            // Make it tall enough to contain the ticks and labels comfortably
            val panelWidth = size.width * 0.95f
            val panelHeight = size.height * 1.15f 
            drawRoundRect(
                color = panelColor,
                topLeft = Offset((size.width - panelWidth)/2, (size.height - panelHeight)/2),
                size = Size(panelWidth, panelHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )

            // 2. DRAW KNOB BODY
            val brush = Brush.radialGradient(
                colors = listOf(knobShineStart, knobShineEnd),
                center = Offset(cx - knobRadius*0.2f, cy - knobRadius*0.2f),
                radius = knobRadius * 1.4f
            )
            drawCircle(brush = brush, radius = knobRadius, center = Offset(cx, cy))
            drawCircle(
                color = Color.Black.copy(alpha = 0.5f),
                radius = knobRadius,
                center = Offset(cx, cy),
                style = Stroke(width = 2f)
            )

            // 3. DRAW NEON ARC
            val zeroPoint = if (minValue <= 0f) 0f else minValue
            if (abs(value - zeroPoint) > 0.1f) {
                val startAngleDeg = -135f + (((zeroPoint - minValue)/(maxValue-minValue)) * 270f)
                val endAngleDeg = -135f + (((value - minValue)/(maxValue-minValue)) * 270f)
                val sweep = endAngleDeg - startAngleDeg
                
                // Glow layer
                drawArc(
                    color = accentColor.copy(alpha = 0.2f),
                    startAngle = if(sweep < 0) endAngleDeg else startAngleDeg,
                    sweepAngle = abs(sweep),
                    useCenter = false,
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )
                // Core layer
                drawArc(
                    color = accentColor,
                    startAngle = if(sweep < 0) endAngleDeg else startAngleDeg,
                    sweepAngle = abs(sweep),
                    useCenter = false,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }

            // 4. DRAW TICKS
            val marks = getMarks(minValue, maxValue)
            for (mark in marks) {
                val markFloat = mark.toFloat()
                if (markFloat < minValue || markFloat > maxValue) continue

                val anglePercent = (markFloat - minValue) / (maxValue - minValue)
                val angleDeg = -135f + (anglePercent * 270f)
                val rad = Math.toRadians(angleDeg.toDouble()).toFloat()
                
                val x1 = cx + tickInner * cos(rad)
                val y1 = cy + tickInner * sin(rad)
                val x2 = cx + tickOuter * cos(rad)
                val y2 = cy + tickOuter * sin(rad)

                val isActive = when {
                    abs(value - zeroPoint) < 0.1f -> abs(markFloat - zeroPoint) < 0.1f
                    value > zeroPoint -> markFloat >= zeroPoint && markFloat <= value
                    else -> markFloat <= zeroPoint && markFloat >= value
                }

                drawLine(
                    color = if (isActive) tickActiveColor else tickNormalColor,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = if(isActive) 3f else 1.5f,
                    cap = StrokeCap.Round
                )
            }

            // 5. DRAW TRIANGULAR POINTER (Direct Angle Calculation)
            val normalizedValue = ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
            val pointerAngleDeg = -135f + (normalizedValue * 270f)
            val pointerRad = Math.toRadians(pointerAngleDeg.toDouble()).toFloat()
            
            val len = knobRadius * 0.85f // Extend further out
            val w = 8f // Wider base for visibility
            
            // Tip
            val tipX = cx + cos(pointerRad) * len
            val tipY = cy + sin(pointerRad) * len
            
            // Base perpendicular vector
            val perpRad = pointerRad + PI.toFloat() / 2f
            val baseHalfW = w / 2f
            
            // Shift base slightly towards center to create arrow shape
            val shift = 0.4f 
            
            val bx1 = cx + (cos(perpRad) * baseHalfW) + (cos(pointerRad) * len * shift)
            val by1 = cy + (sin(perpRad) * baseHalfW) + (sin(pointerRad) * len * shift)
            
            val bx2 = cx - (cos(perpRad) * baseHalfW) + (cos(pointerRad) * len * shift)
            val by2 = cy - (sin(perpRad) * baseHalfW) + (sin(pointerRad) * len * shift)

            val path = Path().apply {
                moveTo(tipX, tipY)
                lineTo(bx1, by1)
                lineTo(bx2, by2)
                close()
            }
            
            drawPath(path, color = accentColor)
        }
        
        // LAYER 2: Labels & Values
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
        ) {
             Text(
                 text = "${value.roundToInt()} $unit",
                 color = accentColor,
                 fontSize = 14.sp, // Larger font
                 fontWeight = FontWeight.Bold
             )
             
             if (label.isNotEmpty()) {
                Text(
                    text = label.uppercase(),
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

private fun getMarks(min: Float, max: Float): List<Int> {
    val range = max - min
    return when {
        min < 0f && max > 0f && range >= 90f -> {
            listOf(-50, -40, -30, -20, -10, 0, 10, 20, 30, 40, 50)
        }
        min == 0f && max == 100f -> {
            listOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
        }
        else -> listOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
    }
}

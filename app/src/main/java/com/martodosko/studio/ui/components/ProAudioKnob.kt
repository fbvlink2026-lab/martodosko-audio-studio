// ==================================================
// FILE: ProAudioKnob.kt
// VERSION: v2.3.0-phase2-compose-legacy-match-v2
// PURPOSE: High-Fidelity Rotary Control matching Legacy KnobView Aesthetics
// NOTE: Fixed Pointer Drawing using manual coords. Added missing imports.
// ==================================================
package com.martodosko.studio.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import kotlin.math.roundToInt // ✅ ADDED IMPORT FOR ROUNDING
import kotlin.math.sin

@Composable
fun ProAudioKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    knobSize: Dp = 100.dp,
    minValue: Float = 0f,
    maxValue: Float = 100f,
    label: String = "",
    unit: String = "%",
    accentColor: Color = Color(0xFF40E0D0),
    isEnabled: Boolean = true
) {
    val normalizedValue = ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    val animatedRotation by animateFloatAsState(targetValue = normalizedValue, label = "rotation")
    
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
                        
                        val delta = -dragAmount.y 
                        val range = abs(maxValue - minValue)
                        val step = range * 0.006f 
                        
                        var newValue = value + (delta * step)
                        newValue = newValue.coerceIn(minValue, maxValue)
                        onValueChange(newValue)
                    }
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val halfSize = minOf(size.width, size.height) * 0.5f
            
            // Colors
            val panelColor = Color(0xFF12232E)
            val knobBgColor = Color(0xFF081218)
            val knobShineStart = Color(0xFF2A5F7A)
            val knobShineEnd = Color(0xFF081218)
            val tickActiveColor = Color(0xFF8AF0E0)
            val tickNormalColor = Color(0xFF3A4A54)
            
            // Dimensions
            val knobRadius = halfSize * 0.54f
            val arcRadius = halfSize * 0.68f
            val tickInner = arcRadius * 1.03f
            val tickOuter = arcRadius * 1.08f

            // 1. DRAW RECTANGULAR PANEL BACKGROUND
            val panelWidth = size.width * 0.9f
            val panelHeight = size.height * 1.2f
            drawRoundRect(
                color = panelColor,
                topLeft = Offset((size.width - panelWidth)/2, (size.height - panelHeight)/2),
                size = Size(panelWidth, panelHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )

            // 2. DRAW KNOB BODY
            val brush = Brush.radialGradient(
                colors = listOf(knobShineStart, knobShineEnd),
                center = Offset(cx - knobRadius*0.3f, cy - knobRadius*0.3f),
                radius = knobRadius * 1.5f
            )
            drawCircle(brush = brush, radius = knobRadius, center = Offset(cx, cy))
            drawCircle(
                color = Color.Black.copy(alpha = 0.4f),
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
                
                drawArc(
                    color = accentColor,
                    startAngle = if(sweep < 0) endAngleDeg else startAngleDeg,
                    sweepAngle = abs(sweep),
                    useCenter = false,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = accentColor.copy(alpha = 0.3f),
                    startAngle = if(sweep < 0) endAngleDeg else startAngleDeg,
                    sweepAngle = abs(sweep),
                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
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
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }

            // 5. DRAW TRIANGULAR POINTER (Manual Calculation - No Save/Restore Needed)
            val pointerAngleDeg = -135f + (normalizedValue * 270f)
            val pointerRad = Math.toRadians(pointerAngleDeg.toDouble()).toFloat()
            
            // Define Triangle Points relative to Center
            val len = knobRadius * 0.75f
            val w = 6f
            
            // Tip of the triangle (points outward)
            val tipX = cx + cos(pointerRad) * len
            val tipY = cy + sin(pointerRad) * len
            
            // Base corners (perpendicular to the angle)
            // Rotate 90 degrees for perpendicular vector
            val perpRad = pointerRad + PI.toFloat() / 2f
            val baseHalfW = w / 2f
            
            val bx1 = cx + cos(perpRad) * baseHalfW
            val by1 = cy + sin(perpRad) * baseHalfW
            
            val bx2 = cx - cos(perpRad) * baseHalfW
            val by2 = cy - sin(perpRad) * baseHalfW
            
            // Shift base inward slightly so it looks like an arrowhead starting from center-ish
            val shiftFactor = 0.3f
            val sbx1 = cx + (bx1 - cx) * shiftFactor
            val sby1 = cy + (by1 - cy) * shiftFactor
            val sbx2 = cx + (bx2 - cx) * shiftFactor
            val sby2 = cy + (by2 - cy) * shiftFactor

            val path = Path().apply {
                moveTo(tipX, tipY)
                lineTo(sbx1, sby1)
                lineTo(sbx2, sby2)
                close()
            }
            
            drawPath(path, color = accentColor)
        }
        
        // LAYER 2: Labels & Values
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
        ) {
             Text(
                 text = "${value.roundToInt()} $unit",
                 color = accentColor,
                 fontSize = 12.sp,
                 fontWeight = FontWeight.Bold
             )
             
             if (label.isNotEmpty()) {
                Text(
                    text = label.uppercase(),
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
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

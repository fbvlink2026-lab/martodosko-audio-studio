// ==================================================
// FILE: ProAudioKnob.kt
// VERSION: v2.3.0-compose-native-fixed
// PURPOSE: Pure Jetpack Compose Rotary Knob with Correct Geometry & Touch Physics
// NOTE: Fixes Y-axis inversion for ticks/pointer and normalizes touch sensitivity by view size.
// ==================================================
package com.martodosko.studio.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
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
    knobSize: Dp = 140.dp,
    minValue: Float = -50f,
    maxValue: Float = 50f,
    label: String = "",
    unit: String = "dB",
    accentColor: Color = Color(0xFF40E0D0),
    isEnabled: Boolean = true
) {
    // Constants
    val ANGLE_START_DEG = 135f   // Bottom Left (7:30 position)
    val ANGLE_END_DEG = 45f      // Bottom Right (4:30 position)
    val TOTAL_SWEEP_DEG = 270f   // Total range
    
    // Helper: Value to Angle (in Degrees, 0=East, Clockwise Positive)
    // We map 0% to 135 deg and 100% to 45+360=405 deg? 
    // Actually, simpler: Map percent to angle between Start and End going CW.
    fun valueToAngle(v: Float): Float {
        val percent = ((v - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
        // Start at 135, go clockwise 270 degrees.
        // In Android/Compose Canvas: 0=Right, 90=Down, 180=Left, 270=Up.
        // So 135 is Down-Left. Adding 270 brings us to 405 (which is 45, Down-Right).
        return ANGLE_START_DEG + (percent * TOTAL_SWEEP_DEG)
    }

    Box(modifier = modifier.size(knobSize)) {
        
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!isEnabled) return@pointerInput
                    
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        
                        // FIX: Normalize sensitivity based on the physical size of the knob in pixels.
                        // Moving up decreases Y coordinate. We want moving UP to increase VALUE.
                        // So we use negative dragAmount.y.
                        // Divide by height to make it independent of screen density/resolution.
                        val deltaY = -dragAmount.y 
                        val normalizedDelta = deltaY / size.height
                        
                        val range = abs(maxValue - minValue)
                        
                        // Multiplier controls speed. 1.0 means full range in one swipe across height.
                        // 0.8 gives slightly finer control.
                        val step = normalizedDelta * range * 0.8f 
                        
                        var newValue = value + step
                        newValue = newValue.coerceIn(minValue, maxValue)
                        
                        onValueChange(newValue)
                    }
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val halfSize = minOf(size.width, size.height) * 0.5f
            
            // Dimensions
            val knobRadius = halfSize * 0.50f   
            val arcRadius = halfSize * 0.65f
            val tickInner = arcRadius * 1.05f
            val tickOuter = arcRadius * 1.15f
            val textRadius = halfSize * 0.85f 

            // Colors
            val colorPanel = Color(0xFF12232E)
            val colorKnobBg = Color(0xFF081218)
            val colorShineStart = Color(0xFF2A5F7A)
            val colorShineEnd = Color(0xFF081218)
            val colorTickActive = Color(0xFF8AF0E0)
            val colorTickNormal = Color(0xFF3A4A54)
            val colorText = Color(0xFFB8DDE6)

            // 1. PANEL BACKGROUND
            val panelW = size.width * 0.95f
            val panelH = size.height * 1.28f 
            drawRoundRect(
                color = colorPanel,
                topLeft = Offset((size.width - panelW)/2, (size.height - panelH)/2),
                size = Size(panelW, panelH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )

            // 2. KNOB BODY
            drawCircle(color = colorKnobBg, radius = knobRadius, center = Offset(cx, cy))
            val brush = Brush.radialGradient(
                colors = listOf(colorShineStart, colorShineEnd),
                center = Offset(cx - knobRadius*0.2f, cy - knobRadius*0.2f),
                radius = knobRadius * 1.5f
            )
            drawCircle(brush = brush, radius = knobRadius, center = Offset(cx, cy))

            // 3. NEON ARC
            val currentValInt = value.roundToInt()
            val zeroPoint = if (minValue <= 0f) 0f else minValue
            
            if (currentValInt != zeroPoint.toInt()) {
                val zeroAngle = valueToAngle(zeroPoint)
                val endAngle = valueToAngle(value)
                
                val startAngle = if (currentValInt > zeroPoint) zeroAngle else endAngle
                val sweepAngle = abs(endAngle - zeroAngle)
                
                // Glow
                drawArc(
                    color = accentColor.copy(alpha = 0.3f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )
                // Core
                drawArc(
                    color = accentColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }

            // 4. TICKS AND NUMBERS (FIXED COORDINATES)
            val marks = getMarks(minValue, maxValue)
            val textMeasurer = rememberTextMeasurer()

            for (mark in marks) {
                val markFloat = mark.toFloat()
                if (markFloat < minValue || markFloat > maxValue) continue

                val angleDeg = valueToAngle(markFloat)
                // Convert to Radians. 
                // CRITICAL: In Screen Space (Y down), sin(angle) gives positive Y for angles 0-180.
                // Our angles are 135 to 405 (or 45).
                // 135 deg -> cos=-0.7, sin=0.7 (Bottom Left). Correct.
                // 270 deg -> cos=0, sin=-1 (Top). Correct.
                // 45 deg -> cos=0.7, sin=0.7 (Bottom Right). Correct.
                val rad = Math.toRadians(angleDeg.toDouble()).toFloat()
                
                val x1 = cx + tickInner * cos(rad)
                val y1 = cy + tickInner * sin(rad) // Y is added because screen Y grows downwards
                val x2 = cx + tickOuter * cos(rad)
                val y2 = cy + tickOuter * sin(rad)

                val isActive = when {
                    currentValInt == zeroPoint.toInt() -> mark == zeroPoint.toInt()
                    currentValInt > zeroPoint -> mark >= zeroPoint && mark <= currentValInt
                    else -> mark <= zeroPoint && mark >= currentValInt
                }

                drawLine(
                    color = if (isActive) colorTickActive else colorTickNormal,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )

                // Draw Number
                val nx = cx + textRadius * cos(rad)
                val ny = cy + textRadius * sin(rad)
                
                val textStr = formatMark(mark, minValue)
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = textStr,
                    topLeft = Offset(nx - 12f, ny - 12f), // Approximate centering
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorText
                    )
                )
            }

            // 5. POINTER (TRIANGLE)
            val indAngle = valueToAngle(value)
            val indLen = knobRadius * 0.80f
            val indW = 8f
            
            val pointerRad = Math.toRadians(indAngle.toDouble()).toFloat()
            
            // Tip
            val tipX = cx + cos(pointerRad) * indLen
            val tipY = cy + sin(pointerRad) * indLen
            
            // Base Perpendicular
            val perpRad = pointerRad + PI.toFloat() / 2f
            val hw = indW / 2f
            val shift = 0.4f 
            
            val b1x = cx + (cos(perpRad) * hw) + (cos(pointerRad) * indLen * shift)
            val b1y = cy + (sin(perpRad) * hw) + (sin(pointerRad) * indLen * shift)
            
            val b2x = cx - (cos(perpRad) * hw) + (cos(pointerRad) * indLen * shift)
            val b2y = cy - (sin(perpRad) * hw) + (sin(pointerRad) * indLen * shift)

            val path = Path().apply {
                moveTo(tipX, tipY)
                lineTo(b1x, b1y)
                lineTo(b2x, b2y)
                close()
            }
            drawPath(path, color = accentColor)
        }
        
        // LABELS
        if (label.isNotEmpty()) {
             Text(
                 text = label.uppercase(),
                 color = Color(0xFFB8DDE6),
                 fontSize = 12.sp,
                 fontWeight = FontWeight.Bold,
                 modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp)
             )
        }
         Text(
             text = "${value.roundToInt()} $unit",
             color = accentColor,
             fontSize = 14.sp,
             fontWeight = FontWeight.Bold,
             modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
         )
    }
}

private fun formatMark(mark: Int, min: Float): String {
    return when {
        mark == 0 -> "0"
        min < 0f && mark > 0 -> "+$mark"
        else -> "$mark"
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

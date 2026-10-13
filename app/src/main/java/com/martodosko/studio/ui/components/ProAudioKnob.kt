// ==================================================
// FILE: ProAudioKnob.kt
// VERSION: v2.3.0-phase2-legacy-exact-match-v4-GEOMETRY-FIX
// PURPOSE: Exact Port with Corrected Angular Geometry
// NOTE: Fixed Tick Positions to match Android Canvas (0deg=East, CW+).
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
    var lastTouchY by remember { mutableStateOf(Float.NaN) }
    
    // LEGACY CONSTANTS
    val ANGLE_TOTAL_RANGE = 270f
    val ANGLE_OFFSET = -90f
    
    // Helper: Maps Value to Angle in Degrees (0=East, Clockwise Positive)
    // Matches Legacy KnobView.valueToAngle()
    fun valueToAngle(v: Float): Float {
        val percent = (v - minValue) / (maxValue - minValue)
        return ANGLE_OFFSET + (percent - 0.5f) * ANGLE_TOTAL_RANGE
    }

    Box(modifier = modifier.size(knobSize)) {
        
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!isEnabled) return@pointerInput
                    
                    detectDragGestures(
                        onDragStart = { pos -> 
                            lastTouchY = pos.position.y 
                        },
                        onDragEnd = { lastTouchY = Float.NaN },
                        onDragCancel = { lastTouchY = Float.NaN }
                    ) { change, dragAmount ->
                        change.consume()
                        
                        if (lastTouchY.isNaN()) {
                            lastTouchY = change.position.y
                            return@detectDragGestures
                        }

                        val currentY = change.position.y
                        val deltaY = lastTouchY - currentY 
                        val safeHeight = maxOf(size.height, 100f) 
                        val range = abs(maxValue - minValue)
                        
                        // Sensitivity tuned for mobile touch feel
                        val step = (deltaY / safeHeight) * range * 0.8f 
                        
                        var newValue = value + step
                        newValue = newValue.coerceIn(minValue, maxValue)
                        
                        onValueChange(newValue)
                        lastTouchY = currentY
                    }
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val halfSize = minOf(size.width, size.height) * 0.5f
            
            // Dimensions matching Legacy proportions
            val knobRadius = halfSize * 0.54f   
            val arcRadius = halfSize * 0.68f
            val tickInner = arcRadius * 1.03f
            val tickOuter = arcRadius * 1.08f

            // Colors
            val colorPanel = Color(0xFF12232E)
            val colorKnobBg = Color(0xFF081218)
            val colorShineStart = Color(0xFF2A5F7A)
            val colorShineEnd = Color(0xFF081218)
            val colorIndicator = accentColor 
            val colorTickActive = Color(0xFF8AF0E0)
            val colorTickNormal = Color(0xFF3A4A54)

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
                
                // Calculate start and sweep for drawArc
                // In Compose/Android Canvas: Start Angle is where it begins, Sweep is how far it goes (CW)
                val startAngle = if (currentValInt > zeroPoint) zeroAngle else endAngle
                val sweepAngle = abs(endAngle - zeroAngle)
                
                // Glow
                drawArc(
                    color = colorIndicator.copy(alpha = 0.3f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )
                // Core
                drawArc(
                    color = colorIndicator,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }

            // 4. TICKS (Fixed Geometry)
            val marks = getMarks(minValue, maxValue)
            for (mark in marks) {
                val markFloat = mark.toFloat()
                if (markFloat < minValue || markFloat > maxValue) continue

                val angleDeg = valueToAngle(markFloat)
                // Convert to Radians for Cos/Sin
                // IMPORTANT: Android/Compose Canvas Y-axis points DOWN.
                // Standard Trig assumes Y points UP.
                // However, since we define 0deg = East and Positive = Clockwise,
                // The formula x = cos(rad), y = sin(rad) actually WORKS for Y-DOWN systems
                // IF we interpret the angle correctly.
                // Let's verify:
                // 0 deg (East): cos(0)=1, sin(0)=0 -> (cx+r, cy). Correct.
                // 90 deg (South/CW): cos(90)=0, sin(90)=1 -> (cx, cy+r). Correct (Down).
                // 180 deg (West): cos(180)=-1, sin(180)=0 -> (cx-r, cy). Correct.
                // 270 deg (North): cos(270)=0, sin(270)=-1 -> (cx, cy-r). Correct (Up).
                
                val rad = Math.toRadians(angleDeg.toDouble()).toFloat()
                
                val x1 = cx + tickInner * cos(rad)
                val y1 = cy + tickInner * sin(rad)
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
            }

            // 5. POINTER (Triangle)
            val indAngle = valueToAngle(value)
            val indLen = knobRadius * 0.75f
            val indW = 6f
            
            val pointerRad = Math.toRadians(indAngle.toDouble()).toFloat()
            
            // Tip
            val tipX = cx + cos(pointerRad) * indLen
            val tipY = cy + sin(pointerRad) * indLen
            
            // Base Perpendicular
            // Rotate 90 degrees CCW for one side, CW for other? 
            // Since Y is down, adding PI/2 rotates Counter-Clockwise visually?
            // Let's just use vector addition perpendicular to the radial line.
            val perpRad = pointerRad + PI.toFloat() / 2f
            val hw = indW / 2f
            val shift = 0.3f 
            
            // Points forming the base of the triangle
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
            drawPath(path, color = colorIndicator)
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

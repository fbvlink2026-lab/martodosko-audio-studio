// ==================================================
// FILE: ProAudioKnob.kt
// VERSION: v2.3.0-phase1-step1
// PURPOSE: Reusable Hyper-realistic Audio Knob Component
// NOTE: Extracted from MainActivity for modularity. Uses Canvas drawing.
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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws a realistic audio knob with metallic gradient and neon pointer.
 *
 * @param value Current value of the knob (raw units).
 * @param onValueChange Callback when user drags the knob.
 * @param modifier Modifier for layout positioning.
 * @param knobSize Diameter of the knob in DP.
 * @param minValue Minimum raw value (e.g., -50f).
 * @param maxValue Maximum raw value (e.g., 50f).
 * @param label Text displayed below the knob.
 * @param accentColor Color of the indicator line and tip.
 * @param isEnabled Whether interaction is allowed.
 */
@Composable
fun ProAudioKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    knobSize: Dp = 80.dp,
    minValue: Float = 0f,
    maxValue: Float = 100f,
    label: String = "",
    accentColor: Color = Color(0xFF40E0D0),
    isEnabled: Boolean = true
) {
    // Normalize value to 0..1 range for rotation calculation
    val normalizedValue = ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    
    // Smooth animation for pointer movement
    val animatedRotation by animateFloatAsState(targetValue = normalizedValue, label = "rotation")

    Box(modifier = modifier.size(knobSize)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!isEnabled) return@pointerInput
                    
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        
                        // Vertical drag sensitivity: Up increases, Down decreases
                        val sensitivity = 0.005f 
                        var newValue = value + (-dragAmount.y * sensitivity * (maxValue - minValue))
                        
                        // Clamp to min/max bounds
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
            drawKnobBody(Offset(centerX, centerY), radius * 0.7f)
            
            // 3. Draw Pointer Line
            drawPointer(Offset(centerX, centerY), radius * 0.6f, animatedRotation, accentColor)
            
            // 4. Draw Center Cap (Screw head look)
            drawCenterCap(Offset(centerX, centerY), radius * 0.2f)
        }
        
        // Optional: Label below the knob
        if (label.isNotEmpty()) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
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

private fun DrawScope.drawKnobBody(center: Offset, radius: Float) {
    // Radial gradient simulating light hitting curved metal
    val lightSourceOffset = Offset(-radius * 0.3f, -radius * 0.3f)
    
    val brush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFEEEEEE), // Highlight (Top Left)
            Color(0xFF999999), // Mid Tone
            Color(0xFF333333)  // Shadow (Bottom Right)
        ),
        center = center + lightSourceOffset,
        radius = radius * 1.5f
    )
    
    drawCircle(brush = brush, radius = radius, center = center)
    
    // Inner Bevel/Shadow for depth
    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        radius = radius,
        center = center,
        style = Stroke(width = 4f)
    )
}

private fun DrawScope.drawPointer(center: Offset, length: Float, rotationNorm: Float, color: Color) {
    // Rotation range: -135 degrees (min) to +135 degrees (max)
    val startAngleDeg = -135f
    val sweepAngleDeg = 270f
    val currentAngleDeg = startAngleDeg + (rotationNorm * sweepAngleDeg)
    val radian = Math.toRadians(currentAngleDeg.toDouble()).toFloat()
    
    val endX = center.x + cos(radian) * length
    val endY = center.y + sin(radian) * length
    
    // Thick line with rounded caps
    drawLine(
        color = color,
        start = center,
        end = Offset(endX, endY),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
    
    // Small dot at the tip for precision
    drawCircle(
        color = color,
        radius = 4f,
        center = Offset(endX, endY)
    )
}

private fun DrawScope.drawCenterCap(center: Offset, radius: Float) {
    // Dark circle in middle (screw cap)
    drawCircle(
        color = Color(0xFF111111),
        radius = radius,
        center = center
    )
    // Tiny highlight on the cap
    drawCircle(
        color = Color.White.copy(alpha = 0.2f),
        radius = radius * 0.5f,
        center = Offset(center.x - radius*0.2f, center.y - radius*0.2f)
    )
}// ==================================================
// FILE: ProAudioKnob.kt
// VERSION: 2.1.0 - FIXED SYNTAX & IMPORTS
// PURPOSE: Hyper-realistic Audio Knob using Canvas
// NOTE: Renamed 'size' param to 'knobSize' to avoid conflict with DrawScope.size
// ==================================================
package com.martodosko.studio.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text // ✅ ADDED
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
import androidx.compose.ui.text.font.FontWeight // ✅ ADDED
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // ✅ ADDED
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Hyper-realistic Audio Knob using Canvas.
 */
@SuppressLint("UnnecessaryInitializer")
@Composable
fun ProAudioKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    knobSize: Dp = 80.dp, // ✅ RENAMED FROM 'size' TO AVOID CONFLICT
    minValue: Float = 0f,
    maxValue: Float = 100f,
    label: String = "",
    accentColor: Color = Color(0xFF40E0D0),
    isEnabled: Boolean = true
) {
    val normalizedValue = ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    val animatedRotation by animateFloatAsState(targetValue = normalizedValue, label = "rotation")

    Box(modifier = modifier.size(knobSize)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!isEnabled) return@pointerInput
                    
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val sensitivity = 0.005f 
                        var newValue = value + (-dragAmount.y * sensitivity * (maxValue - minValue))
                        newValue = newValue.coerceIn(minValue, maxValue)
                        onValueChange(newValue)
                    }
                }
        ) {
            // ✅ USE 'this.size' OR JUST 'size' INSIDE CANVAS SCOPE
            // Since we renamed the parameter, 'size' here refers to DrawScope.size (the actual pixel dimensions)
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val radius = (size.minDimension / 2f) * 0.85f 
            
            drawOuterRing(Offset(centerX, centerY), radius)
            drawKnobBody(Offset(centerX, centerY), radius * 0.7f)
            drawPointer(Offset(centerX, centerY), radius * 0.6f, animatedRotation, accentColor)
            drawCenterCap(Offset(centerX, centerY), radius * 0.2f)
        }
        
        if (label.isNotEmpty()) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold, // Optional styling
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// --- HELPER DRAW FUNCTIONS ---

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

private fun DrawScope.drawKnobBody(center: Offset, radius: Float) {
    val lightSourceOffset = Offset(-radius * 0.3f, -radius * 0.3f)
    
    val brush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFEEEEEE), // Highlight
            Color(0xFF999999), // Mid Tone
            Color(0xFF333333)  // Shadow
        ),
        center = center + lightSourceOffset,
        radius = radius * 1.5f
    )
    
    drawCircle(brush = brush, radius = radius, center = center)
    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        radius = radius,
        center = center,
        style = Stroke(width = 4f)
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
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
    
    drawCircle(
        color = color,
        radius = 4f,
        center = Offset(endX, endY)
    )
}

private fun DrawScope.drawCenterCap(center: Offset, radius: Float) {
    drawCircle(color = Color(0xFF111111), radius = radius, center = center)
    drawCircle(
        color = Color.White.copy(alpha = 0.2f),
        radius = radius * 0.5f,
        center = Offset(center.x - radius*0.2f, center.y - radius*0.2f)
    )
}

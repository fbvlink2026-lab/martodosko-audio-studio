package com.martodosko.studio.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Hyper-realistic Audio Knob using Canvas.
 * Supports min/max values and rotates based on input.
 */
@SuppressLint("UnnecessaryInitializer")
@Composable
fun ProAudioKnob(
    value: Float, // Current value (normalized 0.0 - 1.0 usually, or raw)
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    minValue: Float = 0f,
    maxValue: Float = 100f,
    label: String = "",
    accentColor: Color = Color(0xFF40E0D0),
    isEnabled: Boolean = true
) {
    // Normalize value for rotation calculation
    val normalizedValue = ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    
    // Animation for smooth pointer movement
    val animatedRotation by animateFloatAsState(targetValue = normalizedValue, label = "rotation")

    Box(modifier = modifier.size(size)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!isEnabled) return@pointerInput
                    
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        
                        // Calculate new value based on vertical drag distance
                        // Dragging UP increases value, DOWN decreases
                        val sensitivity = 0.005f 
                        var newValue = value + (-dragAmount.y * sensitivity * (maxValue - minValue))
                        
                        // Clamp
                        newValue = newValue.coerceIn(minValue, maxValue)
                        onValueChange(newValue)
                    }
                }
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.minDimension / 2) * 0.85f // Leave space for ticks
            
            // 1. DRAW OUTER RING (Ticks)
            drawOuterRing(center, radius)
            
            // 2. DRAW KNOB BODY (Metallic Gradient)
            drawKnobBody(center, radius * 0.7f)
            
            // 3. DRAW POINTER LINE
            drawPointer(center, radius * 0.6f, animatedRotation, accentColor)
            
            // 4. DRAW CENTER CAP (Screw head look)
            drawCenterCap(center, radius * 0.2f)
        }
        
        // Optional: Label below the knob
        if (label.isNotEmpty()) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// --- HELPER DRAW FUNCTIONS ---

private fun DrawScope.drawOuterRing(center: Offset, radius: Float) {
    // Draw subtle tick marks around the perimeter
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
    // Radial gradient to simulate light hitting a curved metal surface
    // Light comes from top-left (offset center slightly)
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
    
    drawCircle(
        brush = brush,
        radius = radius,
        center = center
    )
    
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
    // Total sweep = 270 degrees
    val startAngleDeg = -135f
    val sweepAngleDeg = 270f
    val currentAngleDeg = startAngleDeg + (rotationNorm * sweepAngleDeg)
    val radian = Math.toRadians(currentAngleDeg.toDouble()).toFloat()
    
    val endX = center.x + cos(radian) * length
    val endY = center.y + sin(radian) * length
    
    // Draw thick line with rounded caps
    drawLine(
        color = color,
        start = center,
        end = Offset(endX, endY),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
    
    // Add a small dot at the tip for precision
    drawCircle(
        color = color,
        radius = 4f,
        center = Offset(endX, endY)
    )
}

private fun DrawScope.drawCenterCap(center: Offset, radius: Float) {
    // Small dark circle in middle to look like a screw cap
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
}

// ==================================================
// FILE: ProAudioKnob.kt
// VERSION: v2.3.0-phase1-step1-clean
// PURPOSE: Reusable Hyper-realistic Audio Knob Component
// NOTE: Verified clean syntax. No trailing garbage.
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
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

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
            Color(0xFFEEEEEE),
            Color(0xFF999999),
            Color(0xFF333333)
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
    drawCircle(
        color = Color(0xFF111111),
        radius = radius,
        center = center
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.2f),
        radius = radius * 0.5f,
        center = Offset(center.x - radius*0.2f, center.y - radius*0.2f)
    )
}

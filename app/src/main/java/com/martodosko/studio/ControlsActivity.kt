// ==================================================
// FILE: ControlsActivity.kt — BAGONG SIMULA ✅ TAMA ANG PWEStO
// VERSION: 2.0.0 — 0=ITAAS, -50 IBABA-KALIWA, +50 IBABA-KANAN
// UPDATED: 2026-09-14
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class KnobView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var value: Float = 0f  // ✅ SIMULA = 0
        set(newVal) {
            field = newVal.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
        }

    var minValue: Float = -50f
    var maxValue: Float = 50f
    var onValueChange: ((Float) -> Unit)? = null

    // ✅ TAMA NA ANGGULO — 0° = ITAAS, umiikot 270° pababa
    private val ANGLE_0 = -90f      // ⬆️ 0 = ITAAS GITNA
    private val ANGLE_MIN = 135f    // ↙️ -50 = IBABA-KALIWA
    private val ANGLE_MAX = 45f     // ↘️ +50 = IBABA-KANAN
    private var lastTouchY = 0f

    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#2A2A3C") }
    private val paintKnob = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#3E3E5C") }
    private val paintIndicator = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
    }
    private val paintTick = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#777788")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val paintNumber = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 15f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val r = minOf(cx, cy) * 0.35f
        val tickR = r + 12f
        val numR = r + 48f  // ✅ SAKTO — LITAW ANG NUMERO

        // ✅ LAHAT NG NUMERO
        val marks = listOf(-50, -40, -30, -20, -10, 0, 10, 20, 30, 40, 50)

        for (mark in marks) {
            // ✅ KUWENTUHIN ANG ANGGULO — SIMPLE AT TAMA
            val angle = when (mark) {
                0 -> ANGLE_0
                -50 -> ANGLE_MIN
                50 -> ANGLE_MAX
                in -40 downTo -10 -> {
                    val t = (mark + 50f) / 40f
                    ANGLE_0 + t * (ANGLE_MIN - ANGLE_0)
                }
                in 10..40 -> {
                    val t = (mark - 10f) / 40f
                    ANGLE_0 + t * (ANGLE_MAX - ANGLE_0)
                }
                else -> ANGLE_0
            }

            val rad = Math.toRadians(angle.toDouble())
            val x1 = cx + r * cos(rad).toFloat()
            val y1 = cy + r * sin(rad).toFloat()
            val x2 = cx + tickR * cos(rad).toFloat()
            val y2 = cy + tickR * sin(rad).toFloat()
            canvas.drawLine(x1, y1, x2, y2, paintTick)

            // ✅ NUMERO — SAKTO SA PWEStO
            val nx = cx + numR * cos(rad).toFloat()
            val ny = cy + numR * sin(rad).toFloat() + 5f
            val label = when {
                mark == 0 -> "0"
                mark > 0 -> "+$mark"
                else -> "$mark"
            }
            canvas.drawText(label, nx, ny, paintNumber)
        }

        // ✅ KNOB
        canvas.drawCircle(cx, cy, r, paintBg)
        canvas.drawCircle(cx, cy, r * 0.8f, paintKnob)

        // ✅ INDICATOR — TUMUTURO SA TAMA
        val ang = when {
            value == 0f -> ANGLE_0
            value < 0f -> {
                val t = (value + 50f) / 50f
                ANGLE_0 + t * (ANGLE_MIN - ANGLE_0)
            }
            else -> {
                val t = value / 50f
                ANGLE_0 + t * (ANGLE_MAX - ANGLE_0)
            }
        }
        val rad = Math.toRadians(ang.toDouble())
        val len = r * 0.7f
        canvas.drawLine(
            cx, cy,
            cx + len * cos(rad).toFloat(),
            cy + len * sin(rad).toFloat(),
            paintIndicator
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val delta = lastTouchY - event.y
                value += delta / height * 100f * 0.5f
                lastTouchY = event.y
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

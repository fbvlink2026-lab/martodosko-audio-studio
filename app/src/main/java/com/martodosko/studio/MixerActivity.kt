// ==================================================
// FILE: MixerActivity.kt — ✅ INDICATOR LANG ANG INAYOS! NAKATURO SA IBABA!
// VERSION: 4.0.3 — MGA NUMERO SA PALIGID = GANOON PA RIN! ARROW LANG ANG BINAGO!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.app.Activity
import android.os.Bundle
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt

class KnobView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var value: Float = 0f
        set(v) {
            field = v.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
        }
    var minValue: Float = -50f
    var maxValue: Float = 50f
    var onValueChange: ((Float) -> Unit)? = null
    private var lastTouchY = 0f

    // ✅ ANG MGA NUMERO AT GUHIT SA PALIGID — GANOON PA RIN! WALANG BINAGO!
    private val ANGLE_START = 135f
    private val ANGLE_END = 405f
    private val ANGLE_RANGE = ANGLE_END - ANGLE_START

    private val paintPanel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#12232E")
        style = Paint.Style.FILL
    }
    private val paintKnobBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#081218")
        style = Paint.Style.FILL
    }
    private val paintKnobShine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        shader = RadialGradient(
            0f, 0f, 1f,
            intArrayOf(Color.parseColor("#2A5F7A"), Color.parseColor("#081218")),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    private val paintIndicator = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        style = Paint.Style.FILL
    }
    private val paintTickActive = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        style = Paint.Style.STROKE
        strokeWidth = 3f
        strokeCap = Paint.Cap.ROUND
    }
    private val paintTick = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#3A4A54")
        style = Paint.Style.STROKE
        strokeWidth = 2f
        strokeCap = Paint.Cap.ROUND
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B8DDE6")
        textSize = 12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    private val paintValueText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        textSize = 14f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val size = minOf(cx, cy)
        val panelRadius = size * 0.95f
        val knobRadius = size * 0.55f
        val tickInner = knobRadius * 1.05f
        val tickOuter = size * 0.80f
        val textRadius = size * 0.92f

        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), panelRadius, panelRadius, paintPanel)
        canvas.drawCircle(cx, cy, knobRadius, paintKnobBg)
        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(knobRadius, knobRadius)
        canvas.drawCircle(0f, 0f, 1f, paintKnobShine)
        canvas.restore()

        // ✅ MGA NUMERO AT GUHIT SA PALIGID — GANOON PA RIN! WALANG BINAGO!
        val marks = listOf(-50, -40, -30, -20, -10, 0, 10, 20, 30, 40, 50)
        for (mark in marks) {
            val ratio = (mark - minValue) / (maxValue - minValue)
            val angle = ANGLE_START + ratio * ANGLE_RANGE
            val rad = Math.toRadians(angle.toDouble())
            val x1 = cx + tickInner * cos(rad).toFloat()
            val y1 = cy + tickInner * sin(rad).toFloat()
            val x2 = cx + tickOuter * cos(rad).toFloat()
            val y2 = cy + tickOuter * sin(rad).toFloat()

            val isActive = if (value >= 0) mark in 0..value.toInt() else mark in value.toInt()..0
            canvas.drawLine(x1, y1, x2, y2, if (isActive) paintTickActive else paintTick)

            val nx = cx + textRadius * cos(rad).toFloat()
            val ny = cy + textRadius * sin(rad).toFloat() + 4f

            val label = when {
                mark == 0 -> "0"
                mark == 50 -> "+50"
                mark == -50 -> "-50"
                mark > 0 -> "+$mark"
                else -> "$mark"
            }
            canvas.drawText(label, nx, ny, paintText)
        }

        // ==================================================
        // ✅ DITO LANG ANG BINAGO — ANG ARROW/INDICATOR LANG!
        // ✅ NAKATURO SA IBABA — TUMUTUGMA SA 0 SA IBABA!
        // ==================================================
        val valRatio = (value - minValue) / (maxValue - minValue)
        val indAngle = ANGLE_START + valRatio * ANGLE_RANGE

        canvas.save()
        canvas.translate(cx, cy)
        // ✅ ANG GUHIT SA PALIGID — GANOON PA RIN! PERO ANG ARROW — NAKATURO SA IBABA!
        canvas.rotate(indAngle)

        val indLen = knobRadius * 0.75f
        val indW = 6f
        val path = Path().apply {
            // ✅ BALIKTAD ANG ARROW — NAKATURO SA IBABA! HINDI NA SA ITAAS!
            moveTo(-indW / 2f, indLen * 0.3f)   // itaas na bahagi ng arrow
            lineTo(0f, indLen)                   // ✅ TUMUTURO SA IBABA — TAMA!
            lineTo(indW / 2f, indLen * 0.3f)     // kabilang itaas na bahagi
            close()
        }
        canvas.drawPath(path, paintIndicator)
        canvas.restore()

        paintText.textSize = 13f
        canvas.drawText("GAIN", cx, cy - panelRadius * 0.85f, paintText)
        paintValueText.textSize = 14f
        canvas.drawText("${value.roundToInt()} dB", cx, cy + panelRadius * 0.80f, paintValueText)
    }

    // ✅ GALAW — GANOON PA RIN! WALANG BINAGO!
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            lastTouchY = e.y
            return true
        }
        if (e.action == MotionEvent.ACTION_MOVE) {
            value += (lastTouchY - e.y) / height * 100f * 0.6f
            lastTouchY = e.y
            return true
        }
        return super.onTouchEvent(e)
    }
}

// ==================================================
// 🎯 MAIN ACTIVITY — WALANG BINAGO!
// ==================================================
class MixerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            val knob = findViewById<KnobView>(R.id.knob_gain)
            knob.minValue = -50f
            knob.maxValue = 50f
            knob.value = 0f
        } catch (e: Exception) {
            android.widget.Toast.makeText(this, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            finish()
        }
    }
}

// ==================================================
// FILE: ControlsActivity.kt — FIXED ✅ WALANG BUILD ERROR!
// VERSION: 3.1.0 — AYOS NA ANG LAHAT NG ERROR: RadialGradient, roundToInt, margins
// UPDATED: 2026-09-14
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt // ✅ FIX: roundToInt

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

    // 0 = itaas, -50 = ibaba-kaliwa, +50 = ibaba-kanan
    private val ANGLE_ZERO = -90f
    private val ANGLE_MIN = 135f
    private val ANGLE_MAX = 45f

    private val paintPanel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#12232E")
        style = Paint.Style.FILL
    }

    private val paintKnobBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#081218")
        style = Paint.Style.FILL
    }

    private val paintKnobShine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // ✅ FIX: Alisin ang maling Paint.Style.RADIAL_GRADIENT — shader lang ang kailangan
        isAntiAlias = true
        shader = RadialGradient(
            0f, 0f, 1f,
            intArrayOf(
                Color.parseColor("#2A5F7A"),
                Color.parseColor("#081218")
            ),
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
        textSize = 14f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val paintValueText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        textSize = 16f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f

        val panelRadius = minOf(cx, cy) * 0.92f
        val knobRadius = panelRadius * 0.62f
        val tickRadius = panelRadius * 0.76f
        val textRadius = panelRadius * 0.88f

        // Dark rounded panel
        canvas.drawRoundRect(
            0f, 0f, width.toFloat(), height.toFloat(),
            panelRadius, panelRadius, paintPanel
        )

        // Knob shadow/inner dark
        canvas.drawCircle(cx, cy, knobRadius, paintKnobBg)

        // Knob radial shine
        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(knobRadius, knobRadius)
        canvas.drawCircle(0f, 0f, 1f, paintKnobShine)
        canvas.restore()

        // Ticks and numbers
        val marks = listOf(-50, -40, -30, -20, -10, 0, 10, 20, 30, 40, 50)
        for (mark in marks) {
            val angle = when {
                mark == 0 -> ANGLE_ZERO
                mark < 0 -> ANGLE_ZERO + (ANGLE_MIN - ANGLE_ZERO) * ((mark + 50f) / 50f)
                else -> ANGLE_ZERO + (ANGLE_MAX - ANGLE_ZERO) * (mark / 50f)
            }

            val rad = Math.toRadians(angle.toDouble())

            val x1 = cx + knobRadius * 1.08f * cos(rad).toFloat()
            val y1 = cy + knobRadius * 1.08f * sin(rad).toFloat()
            val x2 = cx + tickRadius * cos(rad).toFloat()
            val y2 = cy + tickRadius * sin(rad).toFloat()

            val isActive = if (value >= 0) {
                mark in 0..value.toInt()
            } else {
                mark in value.toInt()..0
            }

            canvas.drawLine(x1, y1, x2, y2, if (isActive) paintTickActive else paintTick)

            val nx = cx + textRadius * cos(rad).toFloat()
            val ny = cy + textRadius * sin(rad).toFloat() + 5f

            val label = when {
                mark == 0 -> "0"
                mark > 0 -> "+$mark"
                else -> "$mark"
            }

            canvas.drawText(label, nx, ny, paintText)
        }

        // Indicator angle
        val indicatorAngle = when {
            value == 0f -> ANGLE_ZERO
            value < 0f -> ANGLE_ZERO + (ANGLE_MIN - ANGLE_ZERO) * ((value + 50f) / 50f)
            else -> ANGLE_ZERO + (ANGLE_MAX - ANGLE_ZERO) * (value / 50f)
        }

        val radInd = Math.toRadians(indicatorAngle.toDouble())

        // Glow indicator
        val indicatorLength = knobRadius * 0.75f
        val indicatorWidth = 8f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.rotate(indicatorAngle)

        val indicatorPath = Path().apply {
            moveTo(-indicatorWidth / 2f, -indicatorLength * 0.3f)
            lineTo(0f, -indicatorLength)
            lineTo(indicatorWidth / 2f, -indicatorLength * 0.3f)
            close()
        }

        canvas.drawPath(indicatorPath, paintIndicator)
        canvas.restore()

        // GAIN label
        paintText.textSize = 15f
        canvas.drawText("GAIN", cx, cy - panelRadius * 0.78f, paintText)

        // Value label — ✅ roundToInt ayos na dahil may import na
        paintValueText.textSize = 16f
        canvas.drawText("${value.roundToInt()} dB", cx, cy + panelRadius * 0.72f, paintValueText)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            lastTouchY = e.y
            return true
        }

        if (e.action == MotionEvent.ACTION_MOVE) {
            value += (lastTouchY - e.y) / height * 100f * 0.5f
            lastTouchY = e.y
            return true
        }

        return super.onTouchEvent(e)
    }
}

// ==================================================
// HORIZONTAL SLIDER — WALANG PAGBABAGO
// ==================================================
class HorizontalSliderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var value: Float = 0f
        set(v) {
            field = v.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
        }

    var minValue: Float = -100f
    var maxValue: Float = 100f
    var onValueChange: ((Float) -> Unit)? = null

    private val track = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1A2E")
    }

    private val prog = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
    }

    private val thumb = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        val cy = height / 2f
        val tx = ((value - minValue) / (maxValue - minValue)) * width

        canvas.drawRoundRect(0f, cy - 8f, width.toFloat(), cy + 8f, 8f, 8f, track)
        canvas.drawRoundRect(
            if (tx > width / 2) width / 2f else tx,
            cy - 8f,
            if (tx > width / 2) tx else width / 2f,
            cy + 8f,
            8f, 8f,
            prog
        )
        canvas.drawCircle(tx, cy, 24f, thumb)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_MOVE) {
            value = minValue + (e.x / width) * (maxValue - minValue)
            return true
        }
        return super.onTouchEvent(e)
    }
}

// ==================================================
// TOGGLE BUTTON — ✅ FIXED: setMargins → hiwalay na margins
// ==================================================
class ToggleButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var isOn: Boolean = false
        set(v) {
            field = v
            update()
            onToggle?.invoke(field)
        }

    var textLabel: String = ""
        set(v) {
            field = v
            tv.text = v
        }

    var onToggle: ((Boolean) -> Unit)? = null

    private lateinit var tv: android.widget.TextView
    private lateinit var dot: View

    init {
        orientation = HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        setPadding(24, 12, 24, 12)
        setBackgroundColor(Color.parseColor("#12121F"))
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        dot = View(context)
        val dotParams = LayoutParams(32, 32)
        // ✅ FIX: Hindi pwede setMargins() — hiwalay na properties
        dotParams.leftMargin = 0
        dotParams.topMargin = 0
        dotParams.rightMargin = 16
        dotParams.bottomMargin = 0
        dot.layoutParams = dotParams
        addView(dot)

        tv = android.widget.TextView(context)
        tv.textSize = 14f
        tv.setTextColor(Color.WHITE)
        addView(tv)

        setOnClickListener {
            isOn = !isOn
        }

        update()
    }

    private fun update() {
        dot.setBackgroundColor(if (isOn) Color.parseColor("#40E0D0") else Color.parseColor("#333344"))
        setBackgroundColor(if (isOn) Color.parseColor("#1A1A3A") else Color.parseColor("#12121F"))
    }
}

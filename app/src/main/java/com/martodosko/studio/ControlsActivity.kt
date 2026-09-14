// ==================================================
// FILE: ControlsActivity.kt — BAGONG SIMULA ✅
// VERSION: 2.0.0 — 0=ITAAS, BAWAT 5, BUONG SAKOP, HINDI SIKSIKAN
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

class KnobView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ✅ DEFAULT = 0 — NASA ITAAS AGAD
    var value: Float = 0f
        set(newVal) {
            field = newVal.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
        }

    var minValue: Float = -50f
    var maxValue: Float = 50f
    var onValueChange: ((Float) -> Unit)? = null

    // ✅ 0 = ITAAS (-90°), -50 = IBABA-KALIWA (135°), +50 = IBABA-KANAN (45°)
    private val angleZero = -90f
    private val angleMin = 135f    // -50
    private val angleMax = 45f     // +50
    private val totalArc = 270f    // Buong sakop mula -50 hanggang +50
    private var lastTouchY = 0f

    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2A2A3C")
        style = Paint.Style.FILL
    }
    private val paintKnob = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#3E3E5C")
        style = Paint.Style.FILL
    }
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
        strokeCap = Paint.Cap.ROUND
    }
    private val paintTickActive = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        style = Paint.Style.STROKE
        strokeWidth = 3f
        strokeCap = Paint.Cap.ROUND
    }
    private val paintNumber = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        textSize = 13f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) * 0.32f

        // ✅ DISTANCE NG NUMERO — MALAYO ENOUGH PARA HINDI SIKSIKAN
        val numberRadius = radius + 58f
        val tickOuter = radius + 12f
        val tickInner = radius + 2f

        // ==================================================
        // ✅ LAHAT NG NUMERO — BAWAT 5: -50, -45, -40 ... 0 ... +40, +45, +50
        // ==================================================
        val marks = listOf(-50, -45, -40, -35, -30, -25, -20, -15, -10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50)

        for (mark in marks) {
            // ✅ KUWENTO NG ANGGULO — 0 = ITAAS, papunta sa kaliwa at kanan
            val angle = when {
                mark == 0 -> angleZero
                mark < 0 -> {
                    // Mula 0 pababa-kaliwa hanggang -50
                    val progress = (mark + 50f) / 50f
                    angleZero + progress * (angleMin - angleZero)
                }
                else -> {
                    // Mula 0 pababa-kanan hanggang +50
                    val progress = mark / 50f
                    angleZero + progress * (angleMax - angleZero)
                }
            }

            val rad = Math.toRadians(angle.toDouble())

            // ✅ GUHIT
            val startX = centerX + tickInner * cos(rad).toFloat()
            val startY = centerY + tickInner * sin(rad).toFloat()
            val endX = centerX + tickOuter * cos(rad).toFloat()
            val endY = centerY + tickOuter * sin(rad).toFloat()
            val tickPaint = if ((value >= 0 && mark in 0..value.toInt()) ||
                               (value < 0 && mark in value.toInt()..0)) paintTickActive else paintTick
            canvas.drawLine(startX, startY, endX, endY, tickPaint)

            // ✅ NUMERO — NAKAPALIBOT, HINDI SIKSIKAN, LITAW LAHAT
            val numX = centerX + numberRadius * cos(rad).toFloat()
            val numY = centerY + numberRadius * sin(rad).toFloat() + 5f
            val label = when {
                mark == 0 -> "0"
                mark > 0 -> "+$mark"
                else -> "$mark"
            }
            canvas.drawText(label, numX, numY, paintNumber)
        }

        // ✅ KNOB
        canvas.drawCircle(centerX, centerY, radius, paintBg)
        canvas.drawCircle(centerX, centerY, radius * 0.8f, paintKnob)

        // ✅ INDICATOR — TUMUTURO SA TAMA
        val currentAngle = when {
            value == 0f -> angleZero
            value < 0f -> {
                val progress = (value + 50f) / 50f
                angleZero + progress * (angleMin - angleZero)
            }
            else -> {
                val progress = value / 50f
                angleZero + progress * (angleMax - angleZero)
            }
        }
        val rad = Math.toRadians(currentAngle.toDouble())
        val indicatorLen = radius * 0.7f
        canvas.drawLine(
            centerX, centerY,
            centerX + indicatorLen * cos(rad).toFloat(),
            centerY + indicatorLen * sin(rad).toFloat(),
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
                val deltaY = lastTouchY - event.y
                val range = maxValue - minValue
                value += deltaY / height * range * 0.5f
                lastTouchY = event.y
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

// ==================================================
// HORIZONTAL SLIDER — WALANG PAGBABAGO
// ==================================================
class HorizontalSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var value: Float = 0f
        set(newVal) { field = newVal.coerceIn(minValue, maxValue); invalidate(); onValueChange?.invoke(field) }
    var minValue: Float = -100f
    var maxValue: Float = 100f
    var onValueChange: ((Float) -> Unit)? = null
    private val paintTrack = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1A1A2E"); style = Paint.Style.FILL }
    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#40E0D0"); style = Paint.Style.FILL }
    private val paintThumb = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFFFFF"); style = Paint.Style.FILL }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = height / 2f
        val thumbRadius = 24f
        canvas.drawRoundRect(0f, centerY - 8f, width.toFloat(), centerY + 8f, 8f, 8f, paintTrack)
        val progressX = ((value - minValue) / (maxValue - minValue)) * width
        if (progressX > width / 2) canvas.drawRoundRect(width / 2f, centerY - 8f, progressX, centerY + 8f, 8f, 8f, paintProgress)
        else canvas.drawRoundRect(progressX, centerY - 8f, width / 2f, centerY + 8f, 8f, 8f, paintProgress)
        canvas.drawCircle(progressX, centerY, thumbRadius, paintThumb)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            value = minValue + (event.x / width) * (maxValue - minValue)
            return true
        }
        return super.onTouchEvent(event)
    }
}

// ==================================================
// TOGGLE BUTTON — WALANG PAGBABAGO
// ==================================================
class ToggleButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var isOn: Boolean = false
        set(newVal) { field = newVal; updateAppearance(); onToggleChange?.invoke(field) }
    var textLabel: String = ""
        set(value) { field = value; labelView.text = value }
    var onToggleChange: ((Boolean) -> Unit)? = null
    private lateinit var labelView: android.widget.TextView
    private lateinit var indicatorView: View

    init {
        orientation = HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        setPadding(24, 12, 24, 12)
        setBackgroundColor(Color.parseColor("#12121F"))
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        indicatorView = View(context)
        indicatorView.layoutParams = LinearLayout.LayoutParams(32, 32).apply { setMargins(0, 0, 16, 0) }
        addView(indicatorView)
        labelView = android.widget.TextView(context)
        labelView.textSize = 14f
        labelView.setTextColor(Color.WHITE)
        addView(labelView)
        setOnClickListener { isOn = !isOn }
        updateAppearance()
    }
    private fun updateAppearance() {
        indicatorView.setBackgroundColor(if (isOn) Color.parseColor("#40E0D0") else Color.parseColor("#333344"))
        setBackgroundColor(if (isOn) Color.parseColor("#1A1A3A") else Color.parseColor("#12121F"))
    }
}

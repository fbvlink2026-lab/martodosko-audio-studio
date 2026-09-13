// ==================================================
// FILE: ControlsActivity.kt — REUSABLE CUSTOM CONTROLS ✅
// VERSION: 1.0.80 — KNOB + HORIZONTAL SLIDER + BUTTONS
// UPDATED: 2026-09-13
// PURPOSE: Reusable na mga kontrol — magagamit sa Mixer, FX, at iba pa
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.abs

// ==================================================
// 🎛️ CUSTOM KNOB — BILOG NA PIHITAN
// ==================================================
class KnobView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var value: Float = 0f
        set(newVal) {
            field = newVal.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
        }

    var minValue: Float = 0f
    var maxValue: Float = 100f
    var onValueChange: ((Float) -> Unit)? = null

    private var startAngle = -135f
    private var endAngle = 135f
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
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) - 8f

        // Background ring
        canvas.drawCircle(centerX, centerY, radius, paintBg)
        // Knob inner
        canvas.drawCircle(centerX, centerY, radius * 0.8f, paintKnob)

        // Indicator line — nakapihit ayon sa value
        val angleRange = endAngle - startAngle
        val progress = (value - minValue) / (maxValue - minValue)
        val currentAngle = startAngle + progress * angleRange
        val rad = Math.toRadians(currentAngle.toDouble())
        val indicatorLength = radius * 0.65

        canvas.drawLine(
            centerX, centerY,
            (centerX + indicatorLength * Math.cos(rad)).toFloat(),
            (centerY + indicatorLength * Math.sin(rad)).toFloat(),
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
                value += deltaY / height * range
                lastTouchY = event.y
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

// ==================================================
// 🎚️ HORIZONTAL SLIDER — PABABA / PATAAS
// ==================================================
class HorizontalSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var value: Float = 0f
        set(newVal) {
            field = newVal.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
        }

    var minValue: Float = -100f
    var maxValue: Float = 100f
    var onValueChange: ((Float) -> Unit)? = null

    private val paintTrack = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1A2E")
        style = Paint.Style.FILL
    }

    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        style = Paint.Style.FILL
    }

    private val paintThumb = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        style = Paint.Style.FILL
        shadowLayer = 4f, 0f, 2f, Color.parseColor("#80000000")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = height / 2f
        val thumbRadius = 24f

        // Track — buong haba
        canvas.drawRoundRect(
            0f, centerY - 8f,
            width.toFloat(), centerY + 8f,
            8f, 8f, paintTrack
        )

        // Progress — mula gitna hanggang thumb
        val progressX = ((value - minValue) / (maxValue - minValue)) * width
        if (progressX > width / 2) {
            canvas.drawRoundRect(
                width / 2f, centerY - 8f,
                progressX, centerY + 8f,
                8f, 8f, paintProgress
            )
        } else {
            canvas.drawRoundRect(
                progressX, centerY - 8f,
                width / 2f, centerY + 8f,
                8f, 8f, paintProgress
            )
        }

        // Thumb — pihitan
        canvas.drawCircle(progressX, centerY, thumbRadius, paintThumb)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                value = minValue + (event.x / width) * (maxValue - minValue)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

// ==================================================
// 🔘 TOGGLE BUTTON — BUKSAN / ISARA
// ==================================================
class ToggleButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var isOn: Boolean = false
        set(newVal) {
            field = newVal
            updateAppearance()
            onToggleChange?.invoke(field)
        }

    var textLabel: String = ""
        set(value) {
            field = value
            labelView.text = value
        }

    var onToggleChange: ((Boolean) -> Unit)? = null

    private lateinit var labelView: TextView
    private lateinit var indicatorView: View

    init {
        orientation = HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        setPadding(24, 12, 24, 12)
        setBackgroundColor(Color.parseColor("#12121F"))
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        // Indicator — bilog na ilaw
        indicatorView = View(context)
        indicatorView.layoutParams = LayoutParams(32, 32).apply {
            setMargins(0, 0, 16, 0)
        }
        addView(indicatorView)

        // Label
        labelView = TextView(context)
        labelView.textSize = 14f
        labelView.setTextColor(Color.WHITE)
        addView(labelView)

        setOnClickListener { isOn = !isOn }
        updateAppearance()
    }

    private fun updateAppearance() {
        indicatorView.setBackgroundColor(
            if (isOn) Color.parseColor("#40E0D0") else Color.parseColor("#333344")
        )
        setBackgroundColor(
            if (isOn) Color.parseColor("#1A1A3A") else Color.parseColor("#12121F")
        )
    }
}

// ==================================================
// FILE: ControlsActivity.kt — BAGONG SIMULA ✅
// VERSION: 2.0.0 — 0=ITAAS, 0-50 BAWAT 5, BUONG BILOG!
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

    // ✅ SIMULA = 0 — NASA ITAAS
    var value: Float = 0f
        set(newVal) {
            field = newVal.coerceIn(0f, 50f)
            invalidate()
            onValueChange?.invoke(field)
        }

    var minValue: Float = 0f
    var maxValue: Float = 50f
    var onValueChange: ((Float) -> Unit)? = null

    // ✅ 0° = ITAAS, umiikot 270° pababa sa kaliwa at kanan
    private val zeroAngle = -90f       // 0 = ITAAS GITNA ⬆️
    private val endLeftAngle = 135f    // 50 = IBABA-KALIWA ↙️
    private val endRightAngle = 45f    // 50 = IBABA-KANAN ↘️
    private val totalRange = 225f      // mula 0 hanggang 50 sa bawat panig
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
        textSize = 14f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) * 0.45f
        val tickOuter = radius + 12f
        val tickInner = radius + 2f
        val numberRadius = radius + 50f

        // ✅ MGA NUMERO: 0,5,10,15,20,25,30,35,40,45,50 — SAKOP ANG BUONG BILOG!
        val marks = listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50)

        for (mark in marks) {
            // ✅ KALIWA = 0→50 pakanan pababa, KANAN = 0→50 pakaliwa pababa
            val angle = when {
                mark == 0 -> zeroAngle
                mark <= 25 -> {
                    // KANAN PABABA: 5→10→15→20→25
                    val progress = mark / 25f
                    zeroAngle + progress * (endRightAngle - zeroAngle)
                }
                else -> {
                    // KALIWA PABABA: 30→35→40→45→50
                    val progress = (mark - 25f) / 25f
                    endRightAngle + progress * (endLeftAngle - endRightAngle)
                }
            }

            val rad = Math.toRadians(angle.toDouble())
            val isActive = mark in 0..value.toInt()
            val tickPaint = if (isActive) paintTickActive else paintTick

            // ✅ GUHIT
            val startX = centerX + tickInner * cos(rad).toFloat()
            val startY = centerY + tickInner * sin(rad).toFloat()
            val endX = centerX + tickOuter * cos(rad).toFloat()
            val endY = centerY + tickOuter * sin(rad).toFloat()
            canvas.drawLine(startX, startY, endX, endY, tickPaint)

            // ✅ NUMERO — LAHAT LITAW!
            val numX = centerX + numberRadius * cos(rad).toFloat()
            val numY = centerY + numberRadius * sin(rad).toFloat() + 5f
            canvas.drawText("$mark", numX, numY, paintNumber)
        }

        // ✅ KNOB
        canvas.drawCircle(centerX, centerY, radius, paintBg)
        canvas.drawCircle(centerX, centerY, radius * 0.8f, paintKnob)

        // ✅ INDICATOR — TUMUTURO SA TAMA!
        val progress = value / 50f
        val currentAngle = zeroAngle + progress * (endLeftAngle - zeroAngle)
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
                value += deltaY / height * 50f * 0.8f
                value = value.coerceIn(0f, 50f)
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
        set(newVal) { field = newVal.coerceIn(0f, 100f); invalidate(); onValueChange?.invoke(field) }
    var onValueChange: ((Float) -> Unit)? = null
    private val paintTrack = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1A1A2E"); style = Paint.Style.FILL }
    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#40E0D0"); style = Paint.Style.FILL }
    private val paintThumb = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFFFFF"); style = Paint.Style.FILL }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = height / 2f
        val thumbRadius = 24f
        canvas.drawRoundRect(0f, centerY - 8f, width.toFloat(), centerY + 8f, 8f, 8f, paintTrack)
        val progressX = (value / 100f) * width
        canvas.drawRoundRect(0f, centerY - 8f, progressX, centerY + 8f, 8f, 8f, paintProgress)
        canvas.drawCircle(progressX, centerY, thumbRadius, paintThumb)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            value = (event.x / width) * 100f
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

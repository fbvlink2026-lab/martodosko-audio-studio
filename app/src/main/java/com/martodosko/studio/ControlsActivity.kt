// ==================================================
// FILE: ControlsActivity.kt — EKSATONG GUSTO MO ✅
// VERSION: 1.0.102 — 0&10 SA IBABA, MAGKATABI, LAHAT NUMERO LITAW!
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

    var value: Float = 0f
        set(newVal) {
            field = newVal.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
        }

    var minValue: Float = -50f
    var maxValue: Float = 50f
    var onValueChange: ((Float) -> Unit)? = null

    // ✅ EKSATONG Pwesto: 0 at 10 NASA PINAKA-IBABA, MAGKATABI, MAY PAGITAN!
    private val totalDegrees = 270f
    private val startAngle = -135f   // pinakakaliwa
    private val endAngle = 135f     // pinakakanan
    private val zeroAngle = -15f     // ✅ 0 = ibaba-KALIWA
    private val tenAngle = 15f       // ✅ 10 = ibaba-KANAN
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

    private val paintTick = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#888899")
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
        textSize = 22f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) - 8f
        val tickOuter = radius + 16f
        val tickInner = radius + 2f
        val numberRadius = radius + 44f

        // ==================================================
        // ✅ GUHIT AT NUMERO — 0 HANGGANG 10 — LAHAT LITAW!
        // ==================================================
        val displayValue = getDisplayValue() // 0-10 mula sa -50 hanggang +50

        for (i in 0..10) {
            // ✅ EKSATONG Pwesto: 0 at 10 NASA PINAKA-IBABA, MAGKATABI!
            val angle = when (i) {
                0 -> zeroAngle
                10 -> tenAngle
                else -> {
                    // 1-9 — pantay na pagitan sa pagitan ng 0 at 10
                    val progress = (i - 1) / 8f
                    zeroAngle + 15f + progress * (tenAngle - zeroAngle - 30f)
                }
            }

            val rad = Math.toRadians(angle.toDouble())
            val isActive = i <= displayValue

            // ✅ GUHIT
            val tickPaint = if (isActive) paintTickActive else paintTick
            val startX = centerX + tickInner * cos(rad).toFloat()
            val startY = centerY + tickInner * sin(rad).toFloat()
            val endX = centerX + tickOuter * cos(rad).toFloat()
            val endY = centerY + tickOuter * sin(rad).toFloat()
            canvas.drawLine(startX, startY, endX, endY, tickPaint)

            // ✅ NUMERO — LITAW SA PALIGID!
            val numX = centerX + numberRadius * cos(rad).toFloat()
            val numY = centerY + numberRadius * sin(rad).toFloat() + 8f
            canvas.drawText("$i", numX, numY, paintNumber)
        }

        // Outer ring
        canvas.drawCircle(centerX, centerY, radius, paintBg)
        // Inner knob
        canvas.drawCircle(centerX, centerY, radius * 0.8f, paintKnob)

        // ✅ INDICATOR — TUMUTURO SA TAMANG NUMERO!
        val dv = displayValue
        val currentAngle = when {
            dv <= 0 -> zeroAngle
            dv >= 10 -> tenAngle
            else -> zeroAngle + (dv / 10f) * (tenAngle - zeroAngle)
        }
        val rad = Math.toRadians(currentAngle.toDouble())
        val indicatorLen = radius * 0.7f

        canvas.drawLine(
            centerX, centerY,
            (centerX + indicatorLen * cos(rad)).toFloat(),
            (centerY + indicatorLen * sin(rad)).toFloat(),
            paintIndicator
        )
    }

    // ✅ I-convert ang tunay na value (-50 hanggang +50) → 0-10 para sa display
    private fun getDisplayValue(): Float {
        val fullRange = maxValue - minValue // 100
        val shifted = value - minValue      // 0 hanggang 100
        return (shifted / fullRange) * 10f  // 0 hanggang 10
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
// 🎚️ HORIZONTAL SLIDER — WALANG PAGBABAGO ✅
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
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = height / 2f
        val thumbRadius = 24f

        canvas.drawRoundRect(
            0f, centerY - 8f,
            width.toFloat(), centerY + 8f,
            8f, 8f, paintTrack
        )

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
// 🔘 TOGGLE BUTTON — WALANG PAGBABAGO ✅
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

    private lateinit var labelView: android.widget.TextView
    private lateinit var indicatorView: View

    init {
        orientation = HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        setPadding(24, 12, 24, 12)
        setBackgroundColor(Color.parseColor("#12121F"))
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        indicatorView = View(context)
        indicatorView.layoutParams = LinearLayout.LayoutParams(32, 32).apply {
            setMargins(0, 0, 16, 0)
        }
        addView(indicatorView)

        labelView = android.widget.TextView(context)
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

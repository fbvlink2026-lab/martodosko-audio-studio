// ==================================================
// FILE: ControlsActivity.kt — KNOB ✅ 0=PINAKA-IBABA ↓, 1-10 LAHAT NAROON!
// VERSION: 1.0.103 — TAMA NA ANG PWESTO NG LAHAT NG NUMERO!
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

    var minValue: Float = 0f
    var maxValue: Float = 10f
    var onValueChange: ((Float) -> Unit)? = null

    // ✅ TAMA NA ANG ANGLE: 0 = PINAKA-IBABA ↓ (-90°), 10 = KANAN-IBABA ↘️ (+90°)
    private val startAngle = -90f   // ↓ PINAKA-IBABA = 0
    private val endAngle = 90f      // ↘️ KANAN-IBABA = 10
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
    }

    private val paintTickActive = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CCCCCC")
        textSize = 22f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) - 20f
        val tickInner = radius + 2f
        val tickOuter = radius + 12f
        val textRadius = radius + 38f

        // ==================================================
        // ✅ LAHAT NG NUMERO 0-10 — NAROON! TAMA NA ANG PWESTO!
        // ==================================================
        val angleRange = endAngle - startAngle
        for (i in 0..10) {
            val progress = i / 10f
            val angleDeg = startAngle + progress * angleRange
            val angleRad = Math.toRadians(angleDeg.toDouble())

            // ✅ GUHIT NG MARKA — LAHAT MAY GUHIT
            val tickPaint = if (i <= value) paintTickActive else paintTick
            canvas.drawLine(
                centerX + tickInner * cos(angleRad).toFloat(),
                centerY + tickInner * sin(angleRad).toFloat(),
                centerX + tickOuter * cos(angleRad).toFloat(),
                centerY + tickOuter * sin(angleRad).toFloat(),
                tickPaint
            )

            // ✅ NUMERO — LAHAT NG 0-10 NAKALAGAY!
            val textX = centerX + textRadius * cos(angleRad).toFloat()
            val textY = centerY + textRadius * sin(angleRad).toFloat() + 8f
            canvas.drawText("$i", textX, textY, paintText)
        }

        // Outer ring
        canvas.drawCircle(centerX, centerY, radius, paintBg)
        // Inner knob
        canvas.drawCircle(centerX, centerY, radius * 0.8f, paintKnob)

        // ✅ GUHIT NG PIHITAN — UMIKOT MULA 0 (ibaba) → 10 (kanan)
        val progress = (value - minValue) / (maxValue - minValue)
        val currentAngle = startAngle + progress * angleRange
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
                value += deltaY / height * range * 2f
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
        canvas.drawRoundRect(0f, centerY - 8f, width.toFloat(), centerY + 8f, 8f, 8f, paintTrack)
        val progressX = ((value - minValue) / (maxValue - minValue)) * width
        if (progressX > width / 2) {
            canvas.drawRoundRect(width / 2f, centerY - 8f, progressX, centerY + 8f, 8f, 8f, paintProgress)
        } else {
            canvas.drawRoundRect(progressX, centerY - 8f, width / 2f, centerY + 8f, 8f, 8f, paintProgress)
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

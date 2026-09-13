// ==================================================
// FILE: ControlsActivity.kt — TAMA NA ANG 0-10 PwestO!
// VERSION: 1.0.101 — 0 at 10 NASA PINAKA-IBABA, MAY PAGITAN!
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

    // ✅ IBALIK ANG TUNAY NA SAKLAW — HINDI LIMITADO SA 0-10!
    var value: Float = 0f
        set(newVal) {
            field = newVal.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
        }

    var minValue: Float = -50f   // ✅ TUNAY NA SAKLAW — tulad ng v1.0.95
    var maxValue: Float = 50f    // ✅ HINDI LIMITADO SA 0-10!
    var onValueChange: ((Float) -> Unit)? = null

    // ==================================================
    // ✅ TAMANG PAGKAKAPwestO: 0 at 10 NASA PINAKA-IBABA!
    // ==================================================
    // Kabuuang anggulo: 270° — mula -135° hanggang +135°
    // 0 = -15° (ibaba-kaliwa), 10 = +15° (ibaba-kanan) — MAGKATABI SA IBABA!
    private val startAngle = -135f   // pinakakaliwa
    private val endAngle = 135f      // pinakakanan
    private val zeroAngle = -15f     // ✅ 0 = ibaba-kaliwa, malapit sa gitna ibaba
    private val tenAngle = 15f       // ✅ 10 = ibaba-kanan, malapit sa gitna ibaba
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

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CCCCCC")
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) - 8f
        val tickOuter = radius + 18f
        val tickInner = radius + 2f
        val textRadius = radius + 42f

        // ==================================================
        // ✅ GUHIT NG LAHAT NG MARKA — 0 hanggang 10
        // ==================================================
        val angleRange = endAngle - startAngle

        // ✅ 0 hanggang 10 — LAHAT MAY GUHIT AT NUMERO!
        for (i in 0..10) {
            // ✅ ESPECIAL: 0 at 10 NASA PINAKA-IBABA, may pagitan ng 1 guhit!
            val angle = when (i) {
                0 -> zeroAngle
                10 -> tenAngle
                else -> {
                    // I-compute ang tamang pwesto para sa 1-9
                    val progress = (i - 1) / 8f  // 1-9 sa pagitan ng 0 at 10
                    val segmentStart = zeroAngle + 15f  // pagitan mula 0
                    val segmentEnd = tenAngle - 15f     // pagitan hanggang 10
                    segmentStart + progress * (segmentEnd - segmentStart)
                }
            }

            val angleRad = Math.toRadians(angle.toDouble())
            val tickPaint = if (i <= normalizeValue(value)) paintTickActive else paintTick

            // Guhit
            val startX = centerX + tickInner * cos(angleRad).toFloat()
            val startY = centerY + tickInner * sin(angleRad).toFloat()
            val endX = centerX + tickOuter * cos(angleRad).toFloat()
            val endY = centerY + tickOuter * sin(angleRad).toFloat()
            canvas.drawLine(startX, startY, endX, endY, tickPaint)

            // ✅ NUMERO — LITAW SA PALIGID!
            val textX = centerX + textRadius * cos(angleRad).toFloat()
            val textY = centerY + textRadius * sin(angleRad).toFloat() + 8f
            canvas.drawText("$i", textX, textY, paintText)
        }

        // Outer ring
        canvas.drawCircle(centerX, centerY, radius, paintBg)
        // Inner knob
        canvas.drawCircle(centerX, centerY, radius * 0.8f, paintKnob)

        // ✅ INDICATOR — TUMUTURO SA TAMANG MARKA!
        val normalized = normalizeValue(value)
        val currentAngle = when {
            normalized <= 0 -> zeroAngle
            normalized >= 10 -> tenAngle
            else -> {
                val progress = (normalized - 0) / 10f
                zeroAngle + progress * (tenAngle - zeroAngle)
            }
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

    // ✅ I-convert ang tunay na value (-50 hanggang 50) → 0-10 para sa display
    private fun normalizeValue(v: Float): Float {
        val fullRange = maxValue - minValue
        val zeroPoint = -minValue  // kung saan ang 0 value
        val normalized = ((v - minValue) / fullRange) * 10f
        return normalized.coerceIn(0f, 10f)
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
                value += deltaY / height * range * 0.8f
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

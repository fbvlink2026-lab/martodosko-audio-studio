// ==================================================
// FILE: ControlsActivity.kt — BAGO ✅ EKSAAKTO SA GUSTO MO!
// VERSION: 2.0.0 — 0=ITAAS, -50 IBABA-KALIWA, +50 IBABA-KANAN
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

    var value: Float = 0f // ✅ SIMULA = 0
        set(newVal) {
            field = newVal.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
        }

    var minValue: Float = -50f
    var maxValue: Float = 50f
    var onValueChange: ((Float) -> Unit)? = null

    // ✅ EKSAAKTONG PWEStO:
    private val angleZero = -90f   // 0 = ITAAS GITNA ⬆️
    private val angleMin = 135f    // -50 = IBABA-KALIWA ↙️
    private val angleMax = 45f     // +50 = IBABA-KANAN ↘️
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
    }
    private val paintTickActive = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val paintNumber = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 14f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val r = minOf(cx, cy) * 0.45f // ✅ Tamang laki ng knob
        val rTick = r + 12f
        val rNum = r + 45f // ✅ Sapat na layo para makita ang numero

        // ✅ LAHAT NG NUMERO
        val marks = listOf(-50, -40, -30, -20, -10, 0, 10, 20, 30, 40, 50)

        for (mark in marks) {
            // ✅ KUWENTO NG ANGGULO — SIMPLE AT TAMA
            val angle = when {
                mark == 0 -> angleZero
                mark == -50 -> angleMin
                mark == 50 -> angleMax
                mark < 0 -> {
                    // -40,-30,-20,-10: mula 0 pababa kaliwa hanggang -50
                    val p = (mark + 50f) / 50f // 0 → 1
                    angleZero + p * (angleMin - angleZero)
                }
                else -> {
                    // 10,20,30,40: mula 0 pababa kanan hanggang +50
                    val p = mark / 50f // 0 → 1
                    angleZero + p * (angleMax - angleZero)
                }
            }

            val rad = Math.toRadians(angle.toDouble())
            val x1 = cx + r * cos(rad).toFloat()
            val y1 = cy + r * sin(rad).toFloat()
            val x2 = cx + rTick * cos(rad).toFloat()
            val y2 = cy + rTick * sin(rad).toFloat()
            val tPaint = if ((value >= 0 && mark in 0..value.toInt()) ||
                             (value < 0 && mark in value.toInt()..0)) paintTickActive else paintTick
            canvas.drawLine(x1, y1, x2, y2, tPaint)

            // ✅ NUMERO — SIGURADONG LITAW
            val nx = cx + rNum * cos(rad).toFloat()
            val ny = cy + rNum * sin(rad).toFloat() + 5f
            val txt = when {
                mark == 0 -> "0"
                mark > 0 -> "+$mark"
                else -> "$mark"
            }
            canvas.drawText(txt, nx, ny, paintNumber)
        }

        // ✅ KNOB
        canvas.drawCircle(cx, cy, r, paintBg)
        canvas.drawCircle(cx, cy, r * 0.8f, paintKnob)

        // ✅ INDICATOR — TUMUTURO SA TAMA
        val ang = when {
            value == 0f -> angleZero
            value < 0f -> {
                val p = (value + 50f) / 50f
                angleZero + p * (angleMin - angleZero)
            }
            else -> {
                val p = value / 50f
                angleZero + p * (angleMax - angleZero)
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

// ==================================================
// HORIZONTAL SLIDER — WALANG PAGBABAGO
// ==================================================
class HorizontalSliderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var value: Float = 0f
        set(v) { field = v.coerceIn(minValue, maxValue); invalidate(); onValueChange?.invoke(field) }
    var minValue: Float = -100f
    var maxValue: Float = 100f
    var onValueChange: ((Float) -> Unit)? = null
    private val track = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1A1A2E") }
    private val prog = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#40E0D0") }
    private val thumb = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }

    override fun onDraw(canvas: Canvas) {
        val cy = height / 2f
        val tx = ((value - minValue) / (maxValue - minValue)) * width
        canvas.drawRoundRect(0f, cy - 8f, width.toFloat(), cy + 8f, 8f, 8f, track)
        canvas.drawRoundRect(if (tx > width/2) width/2f else tx, cy - 8f,
            if (tx > width/2) tx else width/2f, cy + 8f, 8f, 8f, prog)
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
// TOGGLE BUTTON — WALANG PAGBABAGO
// ==================================================
class ToggleButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var isOn: Boolean = false
        set(v) { field = v; update(); onToggle?.invoke(field) }
    var textLabel: String = ""
        set(v) { field = v; tv.text = v }
    var onToggle: ((Boolean) -> Unit)? = null
    private lateinit var tv: android.widget.TextView
    private lateinit var dot: View

    init {
        orientation = HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        setPadding(24,12,24,12)
        setBackgroundColor(Color.parseColor("#12121F"))
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        dot = View(context)
        dot.layoutParams = LayoutParams(32,32).apply { setMargins(0,0,16,0) }
        addView(dot)
        tv = android.widget.TextView(context)
        tv.textSize = 14f
        tv.setTextColor(Color.WHITE)
        addView(tv)
        setOnClickListener { isOn = !isOn }
        update()
    }
    private fun update() {
        dot.setBackgroundColor(if (isOn) Color.parseColor("#40E0D0") else Color.parseColor("#333344"))
        setBackgroundColor(if (isOn) Color.parseColor("#1A1A3A") else Color.parseColor("#12121F"))
    }
}

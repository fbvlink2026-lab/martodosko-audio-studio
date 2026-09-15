// ==================================================
// FILE: MixerActivity.kt — ✅ +40% LARGER GAIN & VALUE TEXT!
// VERSION: 4.7.4 — +40% FONT SIZE! WALANG IBANG BINAGO!
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

    private val ANGLE_TOTAL_RANGE = 270f
    private val ANGLE_OFFSET = -90f

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

    private val paintNeonArc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(6f, 0f, 0f, Color.parseColor("#40E0D0"))
    }

    private val paintTickActive = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8AF0E0")
        style = Paint.Style.STROKE
        strokeWidth = 2f
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
        setShadowLayer(0f, 0f, 0f, android.graphics.Color.TRANSPARENT)
    }
    // ✅ VALUE TEXT — +40% LARGER!
    private val paintValueText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        textSize = 19.6f  // ✅ dating 14f → 14 × 1.4 = 19.6f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        setShadowLayer(0f, 0f, 0f, android.graphics.Color.TRANSPARENT)
    }

    private fun valueToAngle(v: Float): Float {
        val percent = (v - minValue) / (maxValue - minValue)
        return ANGLE_OFFSET + (percent - 0.5f) * ANGLE_TOTAL_RANGE
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val size = minOf(cx, cy)
        val panelRadius = size * 0.95f
        val knobRadius = size * 0.55f
        val arcRadius = size * 0.72f
        val tickInner = arcRadius * 1.03f
        val tickOuter = arcRadius * 1.08f
        val textRadius = size * 0.90f

        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), panelRadius, panelRadius, paintPanel)
        canvas.drawCircle(cx, cy, knobRadius, paintKnobBg)
        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(knobRadius, knobRadius)
        canvas.drawCircle(0f, 0f, 1f, paintKnobShine)
        canvas.restore()

        val currentVal = value.roundToInt()
        if (currentVal != 0) {
            val zeroAngle = ANGLE_OFFSET
            val endAngle = valueToAngle(value)
            
            val startAngle: Float
            val sweepAngle: Float
            
            if (currentVal > 0) {
                startAngle = zeroAngle
                sweepAngle = endAngle - zeroAngle
            } else {
                startAngle = endAngle
                sweepAngle = zeroAngle - endAngle
            }

            canvas.drawArc(
                RectF(cx - arcRadius, cy - arcRadius, cx + arcRadius, cy + arcRadius),
                startAngle,
                sweepAngle,
                false,
                paintNeonArc
            )
        }

        val marks = listOf(-50, -40, -30, -20, -10, 0, 10, 20, 30, 40, 50)
        for (mark in marks) {
            val angle = valueToAngle(mark.toFloat())
            val rad = Math.toRadians(angle.toDouble())
            val x1 = cx + tickInner * cos(rad).toFloat()
            val y1 = cy + tickInner * sin(rad).toFloat()
            val x2 = cx + tickOuter * cos(rad).toFloat()
            val y2 = cy + tickOuter * sin(rad).toFloat()

            val isActive = if (currentVal == 0) {
                mark == 0
            } else if (currentVal > 0) {
                mark in 0..currentVal
            } else {
                mark in currentVal..0
            }
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

        // 🔵 INDICATOR — +90f PARA NAKATURO SA ITAAS
        val indAngle = valueToAngle(value)
        canvas.save()
        canvas.translate(cx, cy)
        canvas.rotate(indAngle + 90f)
        val indLen = knobRadius * 0.75f
        val indW = 6f
        val path = Path().apply {
            moveTo(-indW / 2f, -indLen * 0.3f)
            lineTo(0f, -indLen)
            lineTo(indW / 2f, -indLen * 0.3f)
            close()
        }
        canvas.drawPath(path, paintIndicator)
        canvas.restore()

        // ✅ GAIN — +40% LARGER! dating 13f → 18.2f
        paintText.textSize = 18.2f  // ✅ +40%
        canvas.drawText("GAIN", cx, cy - panelRadius * 1.00f, paintText)
        // ✅ VALUE — +40% LARGER! dating 14f → 19.6f (naka-set na sa taas)
        canvas.drawText("${value.roundToInt()} dB", cx, cy + panelRadius * 0.95f, paintValueText)
    }

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
// 🎛️ HORIZONTAL SLIDER — WALANG BINAGO!
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

    private val track = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1A1A2E") }
    private val prog = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#40E0D0") }
    private val thumb = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }

    override fun onDraw(canvas: Canvas) {
        val cy = height / 2f
        val tx = ((value - minValue) / (maxValue - minValue)) * width
        canvas.drawRoundRect(0f, cy - 8f, width.toFloat(), cy + 8f, 8f, 8f, track)
        canvas.drawRoundRect(
            if (tx > width / 2) width / 2f else tx,
            cy - 8f,
            if (tx > width / 2) tx else width / 2f,
            cy + 8f,
            8f, 8f, prog
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
// 🔘 TOGGLE BUTTON — WALANG BINAGO!
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

    private lateinit var tv: TextView
    private lateinit var dot: View

    init {
        orientation = android.widget.LinearLayout.HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        setPadding(24, 12, 24, 12)
        setBackgroundColor(Color.parseColor("#12121F"))
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        dot = View(context)
        val p = LayoutParams(32, 32)
        p.leftMargin = 0
        p.topMargin = 0
        p.rightMargin = 16
        p.bottomMargin = 0
        dot.layoutParams = p
        addView(dot)

        tv = TextView(context)
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

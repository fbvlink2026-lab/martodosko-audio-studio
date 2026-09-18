// ==================================================
// FILE: KnobView.kt — ✅ LABEL UMANGAT PAITAAS! HINDI NA PUMATONG SA NUMERO!
// VERSION: 5.6.1 — ✅ labelOffsetY = 1.05f — PALAYO SA -10 / 0 / +10!
// UPDATED: 2026-09-18 — WALANG IBANG BINAGO! PANEL = PARISUKAT!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt

open class KnobView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    open var labelText: String = ""
    open var unitText: String = ""
    open var labelOffsetY: Float = 1.05f  // ✅ UMANGAT PAITAAS! HINDI NA PUMATONG SA NUMERO!
    open var valueOffsetY: Float = 0.95f   // ✅ HINDI GINALAW! NASA LUGAR PA RIN!

    var preferenceKey: String? = null

    var value: Float = 0f
        set(v) {
            field = v.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
            saveValue()
        }
    var minValue: Float = -50f
    var maxValue: Float = 50f
    var onValueChange: ((Float) -> Unit)? = null
    private var lastTouchY = 0f

    protected val ANGLE_TOTAL_RANGE = 270f
    protected val ANGLE_OFFSET = -90f

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("KnobValues", Context.MODE_PRIVATE)
    }

    init {
        loadSavedValue()
    }

    private fun saveValue() {
        val key = preferenceKey ?: labelText.ifEmpty { "knob_${id}" }
        if (key.isNotEmpty()) {
            prefs.edit().putFloat(key, value).apply()
        }
    }

    fun loadSavedValue() {
        val key = preferenceKey ?: labelText.ifEmpty { "knob_${id}" }
        if (key.isNotEmpty()) {
            val saved = prefs.getFloat(key, value)
            value = saved
        }
    }

    private fun getMarks(): List<Int> {
        val range = maxValue - minValue
        return when {
            minValue < 0f && maxValue > 0f && range >= 90f -> {
                listOf(-50, -40, -30, -20, -10, 0, 10, 20, 30, 40, 50)
            }
            minValue == 0f && maxValue == 100f -> {
                listOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
            }
            else -> listOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
        }
    }

    private fun formatMark(mark: Int): String {
        return when {
            mark == 0 -> "0"
            minValue < 0f && mark > 0 -> "+$mark"
            else -> "$mark"
        }
    }

    private fun valueToAngle(v: Float): Float {
        val percent = (v - minValue) / (maxValue - minValue)
        return ANGLE_OFFSET + (percent - 0.5f) * ANGLE_TOTAL_RANGE
    }

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
    }
    private val paintLabel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B8DDE6")
        textSize = 18.2f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    private val paintValueText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        textSize = 19.6f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val size = minOf(width, height)
        val halfSize = size * 0.5f
        val panelRadius = size * 0.47f
        val knobRadius = size * 0.27f
        val arcRadius = size * 0.34f
        val tickInner = arcRadius * 1.03f
        val tickOuter = arcRadius * 1.08f
        val textRadius = size * 0.42f

        // ✅ PANEL — PARISUKAT! HINDI HUMABA!
        canvas.drawRect(cx - halfSize, cy - halfSize, cx + halfSize, cy + halfSize, paintPanel)

        canvas.drawCircle(cx, cy, knobRadius, paintKnobBg)
        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(knobRadius, knobRadius)
        canvas.drawCircle(0f, 0f, 1f, paintKnobShine)
        canvas.restore()

        val currentVal = value.roundToInt()
        val zeroPoint = if (minValue <= 0f) 0f else minValue
        if (currentVal != zeroPoint.toInt()) {
            val zeroAngle = valueToAngle(zeroPoint)
            val endAngle = valueToAngle(value)
            val startAngle = if (currentVal > zeroPoint) zeroAngle else endAngle
            val sweepAngle = kotlin.math.abs(endAngle - zeroAngle)
            canvas.drawArc(
                RectF(cx - arcRadius, cy - arcRadius, cx + arcRadius, cy + arcRadius),
                startAngle, sweepAngle, false, paintNeonArc
            )
        }

        val marks = getMarks()
        for (mark in marks) {
            val markFloat = mark.toFloat()
            if (markFloat < minValue || markFloat > maxValue) continue

            val angle = valueToAngle(markFloat)
            val rad = Math.toRadians(angle.toDouble())
            val x1 = cx + tickInner * cos(rad).toFloat()
            val y1 = cy + tickInner * sin(rad).toFloat()
            val x2 = cx + tickOuter * cos(rad).toFloat()
            val y2 = cy + tickOuter * sin(rad).toFloat()

            val isActive = when {
                currentVal == zeroPoint.toInt() -> mark == zeroPoint.toInt()
                currentVal > zeroPoint -> mark in zeroPoint.toInt()..currentVal
                else -> mark in currentVal..zeroPoint.toInt()
            }

            canvas.drawLine(x1, y1, x2, y2, if (isActive) paintTickActive else paintTick)

            val nx = cx + textRadius * cos(rad).toFloat()
            val ny = cy + textRadius * sin(rad).toFloat() + 4f
            canvas.drawText(formatMark(mark), nx, ny, paintText)
        }

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

        // ✅ LABEL — UMANGAT PAITAAS! HINDI NA PUMATONG SA NUMERO!
        if (labelText.isNotEmpty()) {
            canvas.drawText(labelText, cx, cy - panelRadius * labelOffsetY, paintLabel)
        }
        canvas.drawText("${value.roundToInt()} $unitText", cx, cy + panelRadius * valueOffsetY, paintValueText)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            lastTouchY = e.y
            return true
        }
        if (e.action == MotionEvent.ACTION_MOVE) {
            value += (lastTouchY - e.y) / height * (maxValue - minValue) * 0.6f
            lastTouchY = e.y
            return true
        }
        return super.onTouchEvent(e)
    }
}

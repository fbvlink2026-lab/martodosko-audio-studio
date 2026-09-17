// ==================================================
// FILE: KnobView.kt — ✅ BASAHIN NA ANG PANGALAN MULA SA XML! WALANG IBANG PINAGBAGO!
// VERSION: 5.2.0 — ✅ labelText MULA SA android:tag O XML ATTRIBUTES! WALANG TINANGGAL!
// UPDATED: 2026-09-17 — ORIHINAL NA LOGIC + SAVE + TOUCH — LAHAT NANDOON PA RIN!
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
    open var labelOffsetY: Float = 1.07f
    open var valueOffsetY: Float = 0.95f

    // ✅ PANGALAN PARA SA SAVING — BAWAT KNOB MAY SARILING ID!
    var preferenceKey: String? = null

    var value: Float = 0f
        set(v) {
            field = v.coerceIn(minValue, maxValue)
            invalidate()
            onValueChange?.invoke(field)
            saveValue() // ✅ AWTOMATIKONG SAVE — TUWING NAGBABAGO ANG HALAGA!
        }
    var minValue: Float = -50f
    var maxValue: Float = 50f
    var onValueChange: ((Float) -> Unit)? = null
    private var lastTouchY = 0f

    protected val ANGLE_TOTAL_RANGE = 270f
    protected val ANGLE_OFFSET = -90f

    // ✅ SharedPreferences — SARILING MEMORYA NG KNOB!
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("KnobValues", Context.MODE_PRIVATE)
    }

    init {
        // ==============================================
        // ✅ BAGONG DAGDAG — BASAHIN ANG PANGALAN MULA SA XML!
        // ==============================================
        val typedArray = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.tag))
        try {
            // Kung may nakaset na sa code — huwag palitan. Kung wala — kunin mula sa android:tag
            if (labelText.isEmpty()) {
                val tagValue = typedArray.getString(0)
                if (!tagValue.isNullOrEmpty()) {
                    labelText = tagValue
                }
            }
        } finally {
            typedArray.recycle()
        }

        loadSavedValue() // ✅ AGAD BASAHIN ANG NAISAVE NA HALAGA PAGBUKAS!
    }

    // ==============================================
    // ✅ MAG-ISAVE — TUWING NAGBABAGO ANG HALAGA!
    // ==============================================
    private fun saveValue() {
        val key = preferenceKey ?: labelText.ifEmpty { "knob_${id}" }
        if (key.isNotEmpty()) {
            prefs.edit().putFloat(key, value).apply()
        }
    }

    // ==============================================
    // ✅ BALIKAN ANG NAISAVE — PAGBUKAS PA LANG!
    // ==============================================
    fun loadSavedValue() {
        val key = preferenceKey ?: labelText.ifEmpty { "knob_${id}" }
        if (key.isNotEmpty()) {
            val saved = prefs.getFloat(key, value)
            value = saved // ✅ ILOAD ANG NAISAVE — WALANG ANIMASYON, AGAD!
        }
    }

    // ==============================================
    // ✅ LAHAT NG ORIHINAL NA DRAWING CODE — WALANG PINAGBAGO!
    // ==============================================
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
            val startAngle = if (currentVal > 0) zeroAngle else endAngle
            val sweepAngle = if (currentVal > 0) endAngle - zeroAngle else zeroAngle - endAngle
            canvas.drawArc(
                RectF(cx - arcRadius, cy - arcRadius, cx + arcRadius, cy + arcRadius),
                startAngle, sweepAngle, false, paintNeonArc
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
            val isActive = when {
                currentVal == 0 -> mark == 0
                currentVal > 0 -> mark in 0..currentVal
                else -> mark in currentVal..0
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
            value += (lastTouchY - e.y) / height * 100f * 0.6f
            lastTouchY = e.y
            return true
        }
        return super.onTouchEvent(e)
    }
}

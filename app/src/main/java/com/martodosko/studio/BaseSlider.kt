// ==================================================
// FILE: BaseSlider.kt — ✅ PUNDASYON NG LAHAT NG SLIDER!
// VERSION: 1.0.0 — Pahalang na slider + Save + Touch!
// UPDATED: 2026-09-18 — HINDI NA BABAGUHIN!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt

open class BaseSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    open var labelText: String = ""
    open var unitText: String = ""
    open var minValue: Float = 0f
    open var maxValue: Float = 100f
    open var defaultValue: Float = 50f
    open var preferenceKey: String? = null
    open var showCenterMark: Boolean = false
    open var isPrimary: Boolean = false

    var value: Float = 0f
        set(v) {
            field = v.coerceIn(minValue, maxValue)
            invalidate()
            saveValue()
        }

    var onValueChange: ((Float) -> Unit)? = null
    private var lastTouchX = 0f

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("SliderValues", Context.MODE_PRIVATE)
    }

    init {
        loadSavedValue()
    }

    private fun saveValue() {
        val key = preferenceKey ?: "slider_${id}"
        if (key.isNotEmpty()) prefs.edit().putFloat(key, value).apply()
    }

    private fun loadSavedValue() {
        val key = preferenceKey ?: "slider_${id}"
        value = if (key.isNotEmpty()) prefs.getFloat(key, defaultValue) else defaultValue
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            lastTouchX = e.x
            return true
        }
        if (e.action == MotionEvent.ACTION_MOVE) {
            val range = maxValue - minValue
            value += (e.x - lastTouchX) / width * range
            lastTouchX = e.x
            onValueChange?.invoke(value)
            return true
        }
        return super.onTouchEvent(e)
    }

    // Simplified drawing — gagana sa lahat ng slider
    private val paintTrack = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E2D38")
        style = Paint.Style.FILL
    }
    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40E0D0")
        style = Paint.Style.FILL
    }
    private val paintThumb = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        style = Paint.Style.FILL
        setShadowLayer(4f, 0f, 0f, Color.parseColor("#40E0D0"))
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B8DDE6")
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    override fun onDraw(canvas: Canvas) {
        val padTop = 30f
        val padBottom = 30f
        val trackHeight = if (isPrimary) 12f else 8f
        val thumbRadius = if (isPrimary) 18f else 14f
        val left = paddingLeft.toFloat()
        val right = (width - paddingRight).toFloat()
        val top = padTop
        val bottom = height - padBottom
        val cy = (top + bottom) / 2f

        // Background track
        canvas.drawRoundRect(left, cy - trackHeight/2, right, cy + trackHeight/2, trackHeight/2, trackHeight/2, paintTrack)

        // Progress
        val percent = (value - minValue) / (maxValue - minValue)
        val progressX = left + percent * (right - left)
        canvas.drawRoundRect(left, cy - trackHeight/2, progressX, cy + trackHeight/2, trackHeight/2, trackHeight/2, paintProgress)

        // Center mark
        if (showCenterMark) {
            val centerX = (left + right) / 2f
            canvas.drawLine(centerX, cy - 15f, centerX, cy + 15f, Paint().apply {
                color = Color.parseColor("#6A7A84")
                strokeWidth = 2f
            })
        }

        // Thumb
        canvas.drawCircle(progressX, cy, thumbRadius, paintThumb)

        // Label & Value
        paintText.textSize = 14f
        canvas.drawText(labelText, left + 50f, top - 5f, paintText)
        paintText.textSize = 16f
        canvas.drawText("${value.roundToInt()} $unitText", right - 50f, top - 5f, paintText)
    }
}

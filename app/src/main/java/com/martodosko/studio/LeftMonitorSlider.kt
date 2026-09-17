// ==================================================
// FILE: LeftMonitorSlider.kt — ✅ LEFT MONITOR OUTPUT!
// VERSION: 1.0.0 — Kaliwang monitor/speaker level
// UPDATED: 2026-09-18
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class LeftMonitorSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseSlider(context, attrs, defStyleAttr) {
    init {
        labelText = "L OUT"
        unitText = "dB"
        minValue = -50f
        maxValue = 10f
        defaultValue = 0f
        preferenceKey = "left_monitor"
    }
}

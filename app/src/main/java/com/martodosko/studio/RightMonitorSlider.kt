// ==================================================
// FILE: RightMonitorSlider.kt — ✅ RIGHT MONITOR OUTPUT!
// VERSION: 1.0.0 — Kanang monitor/speaker level
// UPDATED: 2026-09-18
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class RightMonitorSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseSlider(context, attrs, defStyleAttr) {
    init {
        labelText = "R OUT"
        unitText = "dB"
        minValue = -50f
        maxValue = 10f
        defaultValue = 0f
        preferenceKey = "right_monitor"
    }
}

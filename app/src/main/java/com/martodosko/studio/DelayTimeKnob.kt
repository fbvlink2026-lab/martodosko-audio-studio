// ==================================================
// FILE: DelayTimeKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — DELAY TIME: 10 hanggang 1000ms
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class DelayTimeKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "TIME"
        unitText = "ms"
        minValue = 10f
        maxValue = 1000f
        preferenceKey = "delay_time"
    }
}

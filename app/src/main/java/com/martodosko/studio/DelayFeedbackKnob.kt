// ==================================================
// FILE: DelayFeedbackKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — DELAY FEEDBACK: 0 hanggang 100%
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class DelayFeedbackKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "FEEDBACK"
        unitText = "%"
        minValue = 0f
        maxValue = 100f
        preferenceKey = "delay_feedback"
    }
}

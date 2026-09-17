// ==================================================
// FILE: EqHighKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — EQ HIGH: -12 hanggang +12 dB
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class EqHighKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "HIGH"
        unitText = "dB"
        minValue = -12f
        maxValue = 12f
        preferenceKey = "eq_high"
    }
}

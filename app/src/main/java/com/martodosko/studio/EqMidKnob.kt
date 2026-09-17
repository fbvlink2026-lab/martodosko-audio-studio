// ==================================================
// FILE: EqMidKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — EQ MID: -12 hanggang +12 dB
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class EqMidKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "MID"
        unitText = "dB"
        minValue = -12f
        maxValue = 12f
        preferenceKey = "eq_mid"
    }
}

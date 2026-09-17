// ==================================================
// FILE: CompThresholdKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — COMPRESSOR THRESHOLD: -60 hanggang 0 dB
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class CompThresholdKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "THRESHOLD"
        unitText = "dB"
        minValue = -60f
        maxValue = 0f
        preferenceKey = "comp_threshold"
    }
}

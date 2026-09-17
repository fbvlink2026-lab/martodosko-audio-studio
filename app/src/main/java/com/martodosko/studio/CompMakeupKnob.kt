// ==================================================
// FILE: CompMakeupKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — COMPRESSOR MAKEUP: 0 hanggang 24 dB
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class CompMakeupKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "MAKEUP"
        unitText = "dB"
        minValue = 0f
        maxValue = 24f
        preferenceKey = "comp_makeup"
    }
}

// ==================================================
// FILE: CompRatioKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — COMPRESSOR RATIO: 1 hanggang 20:1
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class CompRatioKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "RATIO"
        unitText = ":1"
        minValue = 1f
        maxValue = 20f
        preferenceKey = "comp_ratio"
    }
}

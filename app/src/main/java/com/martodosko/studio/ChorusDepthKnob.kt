// ==================================================
// FILE: ChorusDepthKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — CHORUS DEPTH: 0 hanggang 100
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class ChorusDepthKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "DEPTH"
        unitText = ""
        minValue = 0f
        maxValue = 100f
        preferenceKey = "chorus_depth"
    }
}

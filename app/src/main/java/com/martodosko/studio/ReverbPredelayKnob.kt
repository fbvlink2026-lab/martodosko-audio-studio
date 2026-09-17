// ==================================================
// FILE: ReverbPredelayKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — REVERB PRE-DELAY: 0 hanggang 100ms
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class ReverbPredelayKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "PRE-DELAY"
        unitText = "ms"
        minValue = 0f
        maxValue = 100f
        preferenceKey = "reverb_predelay"
    }
}

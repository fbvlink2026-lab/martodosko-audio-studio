// ==================================================
// FILE: ReverbDecayKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — REVERB DECAY: 0.1 hanggang 5s
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class ReverbDecayKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "DECAY"
        unitText = "s"
        minValue = 0.1f
        maxValue = 5f
        preferenceKey = "reverb_decay"
    }
}

// ==================================================
// FILE: CompAttackKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — COMPRESSOR ATTACK: 0.1 hanggang 100ms
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class CompAttackKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "ATTACK"
        unitText = "ms"
        minValue = 0.1f
        maxValue = 100f
        preferenceKey = "comp_attack"
    }
}

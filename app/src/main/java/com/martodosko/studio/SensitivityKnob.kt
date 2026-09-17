// ==================================================
// FILE: SensitivityKnob.kt — ✅ SARILING KNOB! NAGMAMANA SA KnobView!
// VERSION: 1.0.0 — KUSANG MAY LABEL, RANGE AT SAVE KEY!
// UPDATED: 2026-09-17
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class SensitivityKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "SENS"
        unitText = ""
        minValue = 0f
        maxValue = 100f
        preferenceKey = "sensitivity_value"
    }
}

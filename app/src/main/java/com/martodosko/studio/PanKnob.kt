// ==================================================
// FILE: PanKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — PAN: -100 (L) hanggang +100 (R)
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class PanKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "PAN"
        unitText = ""
        minValue = -100f
        maxValue = 100f
        preferenceKey = "pan"
    }
}

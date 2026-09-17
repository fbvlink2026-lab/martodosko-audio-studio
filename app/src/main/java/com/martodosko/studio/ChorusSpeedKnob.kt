// ==================================================
// FILE: ChorusSpeedKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — CHORUS SPEED: 0.1 hanggang 10Hz
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class ChorusSpeedKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "SPEED"
        unitText = "Hz"
        minValue = 0.1f
        maxValue = 10f
        preferenceKey = "chorus_speed"
    }
}

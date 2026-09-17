// ==================================================
// FILE: DistortionToneKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — DISTORTION TONE: 0 hanggang 100
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class DistortionToneKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "TONE"
        unitText = ""
        minValue = 0f
        maxValue = 100f
        preferenceKey = "dist_tone"
    }
}

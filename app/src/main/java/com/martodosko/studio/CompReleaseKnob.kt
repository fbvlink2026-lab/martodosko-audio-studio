// ==================================================
// FILE: CompReleaseKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — COMPRESSOR RELEASE: 10 hanggang 500ms
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class CompReleaseKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "RELEASE"
        unitText = "ms"
        minValue = 10f
        maxValue = 500f
        preferenceKey = "comp_release"
    }
}

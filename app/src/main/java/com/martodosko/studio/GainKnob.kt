// ==================================================
// FILE: GainKnob.kt — ✅ PANG-GAIN LANG! SARILING PANGALAN!
// VERSION: 5.0.0 — INHERIT KAY KnobView! WALANG DOBLENG CODE!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class GainKnob @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {

    // ✅ SARILING PANGALAN AT SETTINGS — HINDI NA BABAGUHIN ANG KnobView!
    override var labelText: String = "GAIN"
    override var unitText: String = "dB"
    override var labelOffsetY: Float = 1.07f  // ✅ INILAYO NG 7% — HINDI DUMIKIT SA 0!
    override var valueOffsetY: Float = 0.95f

    init {
        minValue = -50f
        maxValue = 50f
        value = 0f
    }
}

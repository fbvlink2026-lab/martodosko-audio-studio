// ==================================================
// FILE: PanSlider.kt — ✅ LEFT ↔ RIGHT BALANCE SLIDER!
// VERSION: 1.0.0 — -100 (L) ↔ 0 (CENTER) ↔ +100 (R)
// UPDATED: 2026-09-18
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class PanSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseSlider(context, attrs, defStyleAttr) {
    init {
        labelText = "BALANCE"
        unitText = ""
        minValue = -100f
        maxValue = 100f
        defaultValue = 0f
        preferenceKey = "pan_slider"
        showCenterMark = true // ✅ May mark sa gitna (Center)
    }
}

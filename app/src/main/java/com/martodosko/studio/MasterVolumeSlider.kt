// ==================================================
// FILE: MasterVolumeSlider.kt — ✅ MASTER VOLUME FADER!
// VERSION: 1.0.0 — PANGKALAHATANG TUNOG NG BUONG MIXER!
// UPDATED: 2026-09-18
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class MasterVolumeSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseSlider(context, attrs, defStyleAttr) {
    init {
        labelText = "MASTER"
        unitText = "dB"
        minValue = -60f
        maxValue = 12f
        defaultValue = 0f
        preferenceKey = "master_volume"
        isPrimary = true // ✅ Mas malaki, mas makapal — MASTER!
    }
}

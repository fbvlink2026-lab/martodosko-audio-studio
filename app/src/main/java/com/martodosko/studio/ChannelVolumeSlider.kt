// ==================================================
// FILE: ChannelVolumeSlider.kt — ✅ CHANNEL FADER!
// VERSION: 1.0.0 — -∞ hanggang +10 dB
// UPDATED: 2026-09-18
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class ChannelVolumeSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseSlider(context, attrs, defStyleAttr) {
    init {
        labelText = "CHANNEL"
        unitText = "dB"
        minValue = -50f
        maxValue = 10f
        defaultValue = 0f
        preferenceKey = "channel_volume_slider"
    }
}

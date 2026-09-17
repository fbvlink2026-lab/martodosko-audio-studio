// ==================================================
// FILE: ChannelVolumeKnob.kt — ✅ SARILING KNOB!
// VERSION: 1.0.0 — CHANNEL VOLUME: -50 hanggang +10 dB
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.util.AttributeSet

class ChannelVolumeKnob @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KnobView(context, attrs, defStyleAttr) {
    init {
        labelText = "VOLUME"
        unitText = "dB"
        minValue = -50f
        maxValue = 10f
        preferenceKey = "channel_volume"
    }
}

package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.roundToInt

class MixerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        val gainKnob = findViewById<KnobView>(R.id.knob_gain)
        val gainValue = findViewById<TextView>(R.id.tv_gain_value)

        gainKnob.minValue = -50f
        gainKnob.maxValue = 50f
        gainKnob.value = 0f  // ✅ SIMULA = 0

        gainKnob.onValueChange = { newVal ->
            gainValue.text = "${newVal.roundToInt()} dB"
        }
    }
}

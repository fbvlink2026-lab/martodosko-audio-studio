package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class MixerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)
        try {
            val knob = findViewById<KnobView>(R.id.knob_gain)
            val tv = findViewById<TextView>(R.id.tv_gain_value)
            knob.minValue = -50f
            knob.maxValue = 50f
            knob.value = 0f // ✅ SIMULA = 0
            tv.text = "0 dB"
            knob.onValueChange = { tv.text = "${it.roundToInt()} dB" }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}

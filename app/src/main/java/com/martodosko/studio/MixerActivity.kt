// ==================================================
// FILE: MixerActivity.kt - BAGONG SIMULA ✅
// VERSION: 2.0.0 - SIMULA = 0 dB
// UPDATED: 2026-09-14
// ==================================================
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
            val gainKnob = findViewById<KnobView>(R.id.knob_gain)
            val gainValue = findViewById<TextView>(R.id.tv_gain_value)

            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f
            gainKnob.value = 0f  // ✅ SIGURADONG 0 ANG SIMULA!
            gainValue.text = "0 dB"

            gainKnob.onValueChange = { newVal ->
                gainValue.text = "${newVal.roundToInt()} dB"
            }

            Toast.makeText(this, "0 dB — Ready", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            finish()
        }
    }
}
